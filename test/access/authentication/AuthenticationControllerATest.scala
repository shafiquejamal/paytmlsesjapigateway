package access.authentication

import java.io.File
import java.util.UUID

import access.registration.ActivationCodeGenerator
import access.{JWTParamsProvider, TestJWTParamsProviderImpl}
import com.typesafe.config.ConfigFactory
import communication.{Emailer, TestEmailerImpl}
import db.{DBConfig, InitialMigration, OneAppPerTestWithOverrides, ScalikeJDBCTestDBConfig}
import org.scalatest._
import pdi.jwt.JwtJson
import play.api.Configuration
import play.api.http.HeaderNames
import play.api.inject._
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import scalikejdbc.NamedAutoSession
import user.UserFixture
import util.{TestTimeProviderImpl, TestUUIDProviderImpl, TimeProvider, UUIDProvider}

class AuthenticationControllerATest
  extends FlatSpec
  with ShouldMatchers
  with OneAppPerTestWithOverrides
  with BeforeAndAfterEach
  with InitialMigration
  with UserFixture {

  override def overrideModules =
    Seq(
      bind[DBConfig].to[ScalikeJDBCTestDBConfig],
      bind[JWTParamsProvider].to[TestJWTParamsProviderImpl],
      bind[UUIDProvider].to[TestUUIDProviderImpl],
      bind[Emailer].to[TestEmailerImpl],
      bind[TimeProvider].to[TestTimeProviderImpl]
    )

  val dBConfig = new ScalikeJDBCTestDBConfig()
  val newPassword = "some new password"
  val timeProvider = new TestTimeProviderImpl()
  val configuration =
    new Configuration(ConfigFactory.parseFile(new File("conf/application.conf")).resolve())

  override def beforeEach() {
    implicit val session = NamedAutoSession(Symbol(dBConfig.dBName))
    dBConfig.setUpAllDB()
    migrate(dBConfig)
    sqlToAddUsers.foreach(_.update.apply())
    super.beforeEach()
  }

  override def afterEach() {
    dBConfig.closeAll()
    super.afterEach()
  }

  val jWTParamsProvider = new TestJWTParamsProviderImpl()
  val claim =
    Json.obj("userId" -> UUID.fromString("00000000-0000-0000-0000-000000000001"),
             "iat" -> timeProvider.now())
  val expectedJWT = JwtJson.encode(claim, jWTParamsProvider.secretKey, jWTParamsProvider.algorithm)

  trait JWTChecker {
    def checkJWT(authentication:JsValue) = {
      (contentFromRequest(authentication) \ "token").asOpt[String] should contain(expectedJWT)
      (contentFromRequest(authentication) \ "username").asOpt[String] should contain("alice")
      (contentFromRequest(authentication) \ "email").asOpt[String] should contain("alice@alice.com")
    }
  }

  "authentication" should "return a valid jwt if the credentials are valid - using username" in new JWTChecker {
    val authentication = Json.toJson(Map("username" -> "aLICe", "email" -> "  ", "password" -> "passwordAliceID2"))

    checkJWT(authentication)
  }

  it should "return a valid jwt if the credentials are valid - using email" in new JWTChecker {
    val authentication = Json.toJson(Map("username" -> "  ", "email" -> "AlicE@aLICe.Com", "password" -> "passwordAliceID2"))

    checkJWT(authentication)
  }

  it should "return a valid jwt if the credentials are valid - using both username and email where only the email matches " +
  "the password" in new JWTChecker {
    val authentication =
      Json.toJson(
        Map("username" -> "some-non-existent-user", "email" -> " alice@alice.com ", "password" -> "passwordAliceID2"))

    checkJWT(authentication)
  }

  it should "return a valid jwt if the credentials are valid - using both username and email where only the username " +
  "matches the password" in new JWTChecker {
    val authentication =
      Json.toJson(
        Map("username" -> " alice ", "email" -> "some-non-existent-email@email.com", "password" -> "passwordAliceID2"))

    checkJWT(authentication)
  }

  it should "fail if neither the username or email match" in {
    val authentication = Json.toJson(Map("username" -> "alice", "email" -> "alice@alice.com", "password" -> "wong password"))

    (contentFromRequest(authentication) \ "status").asOpt[String] should contain("authentication failed")
  }

  it should "fail if the data is not valid" in {
    val authentication = Json.toJson(Map("username" -> "", "email" -> "", "password" -> ""))

    (contentFromRequest(authentication) \ "status").asOpt[String] should contain("invalid data")
  }

  "sending a password reset code" should "succeed" in {
    val message = Json.obj("email" -> "some@user.com")
    val result =
      route(app, FakeRequest(POST, "/send-password-reset-link")
      .withJsonBody(message)
      .withHeaders(HeaderNames.CONTENT_TYPE -> "application/json"))
      .get
    status(result) shouldBe OK
  }

  it should "fail if the message is bad" in {
    val message = Json.obj("garbage" -> "some@user.com")
    val result =
      route(app, FakeRequest(POST, "/send-password-reset-link")
      .withJsonBody(message)
      .withHeaders(HeaderNames.CONTENT_TYPE -> "application/json"))
      .get
    status(result) shouldBe BAD_REQUEST
  }

  "resetting the password" should "succeed if the code matches the email in the db, and fail if the code is used a " +
  "second time" in new JWTChecker {
    val key = configuration.getString(ActivationCodeGenerator.configurationKey).getOrElse("")
    val hashedCode = ActivationCodeGenerator.generateWithDashes(passwordResetCodeAlice2, key)
    val message = Json.obj("email" -> "alice@alice.com", "code" -> hashedCode, "newPassword" -> newPassword)
    val result =
      route(app, FakeRequest(POST, "/reset-password")
      .withJsonBody(message)
      .withHeaders(HeaderNames.CONTENT_TYPE -> "application/json"))
      .get

    status(result) shouldBe OK

    val authentication =
      Json.toJson(Map("username" -> " alice ", "email" -> "some-non-existent-email@email.com", "password" -> newPassword))

    checkJWT(authentication)

    val anotherNewPassword = "another new password"
    val messageSecondUse =
      Json.obj("email" -> "alice@alice.com", "code" -> passwordResetCodeAlice2, "newPassword" -> anotherNewPassword)
    val resultSecondUse =
      route(app, FakeRequest(POST, "/reset-password")
      .withJsonBody(messageSecondUse)
      .withHeaders(HeaderNames.CONTENT_TYPE -> "application/json"))
      .get

    status(resultSecondUse) shouldBe BAD_REQUEST

    val authenticationSecondUse =
      Json.toJson(Map("username" -> " alice ", "email" -> "some-non-existent-email@email.com",
        "password" -> anotherNewPassword))

    (contentFromRequest(authenticationSecondUse) \ "token").asOpt[String] shouldBe empty
  }

  it should "fail if the code does not match the email in the db" in {
    val message = Json.obj("email" -> "alice@alice.com", "code" -> passwordResetCodeAlice1, "newPassword" -> newPassword)
    val result =
      route(app, FakeRequest(POST, "/reset-password")
      .withJsonBody(message)
      .withHeaders(HeaderNames.CONTENT_TYPE -> "application/json"))
      .get
    status(result) shouldBe BAD_REQUEST
  }

  it should "fail if the message is malformed" in {
    val message = Json.obj("bad" -> "alice@alice.com", "code" -> passwordResetCodeAlice1, "newPassword" -> newPassword)
    val result =
      route(app, FakeRequest(POST, "/reset-password")
      .withJsonBody(message)
      .withHeaders(HeaderNames.CONTENT_TYPE -> "application/json"))
      .get
    status(result) shouldBe BAD_REQUEST
  }

  "Logging out of all devices" should "succeed if the user exists" in {
    val timeProvider = new TestTimeProviderImpl()
    val jWTParamsProvider = new TestJWTParamsProviderImpl()
    val claim = Json.obj("userId" -> id1, "iat" -> timeProvider.now())
    val jWT = JwtJson.encode(claim, jWTParamsProvider.secretKey, jWTParamsProvider.algorithm)

    val result = route(app, FakeRequest(POST, "/logout-all-devices")
      .withHeaders(("Authorization", "Bearer " + jWT))
      .withJsonBody(Json.obj("currentPassword" -> "passwordBobID3", "newPassword" -> "some-new-password",
      "iat" -> timeProvider.now()))).get

    status(result) shouldBe OK
    (contentAsJson(result) \ "status").asOpt[String] should contain("success")
  }

  it should "fail if the user does not exist" in {
    val timeProvider = new TestTimeProviderImpl()
    val jWTParamsProvider = new TestJWTParamsProviderImpl()
    val claim = Json.obj("userId" -> idNonExistentUser, "iat" -> timeProvider.now())
    val jWT = JwtJson.encode(claim, jWTParamsProvider.secretKey, jWTParamsProvider.algorithm)

    val result = route(app, FakeRequest(POST, "/logout-all-devices")
      .withHeaders(("Authorization", jWT))
      .withJsonBody(Json.obj("currentPassword" -> "", "newPassword" -> "some-new-password",
      "iat" -> timeProvider.now()))).get

    status(result) shouldBe UNAUTHORIZED
  }

  private def contentFromRequest(postData:JsValue, path:String = "/authenticate"):JsValue =
    contentAsJson(
      route(app, FakeRequest(POST, path)
      .withJsonBody(postData)
      .withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")
      ).get
    )

}
