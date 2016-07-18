package access.authentication

import java.io.File
import java.util.UUID

import access.{JWTParamsProvider, TestJWTParamsProviderImpl}
import com.typesafe.config.ConfigFactory
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
import util.{PlayConfigParamsProvider, TestUUIDProviderImpl, UUIDProvider}

class AuthenticationControllerATest
  extends FlatSpec
  with ShouldMatchers
  with OneAppPerTestWithOverrides
  with BeforeAndAfterEach
  with InitialMigration
  with UserFixture {

  override def overrideModules =
    Seq(bind[DBConfig].to[ScalikeJDBCTestDBConfig],
        bind[JWTParamsProvider].to[TestJWTParamsProviderImpl],
        bind[UUIDProvider].to[TestUUIDProviderImpl]
       )

  val dBConfig =
    new ScalikeJDBCTestDBConfig(
      new PlayConfigParamsProvider(new Configuration(ConfigFactory.parseFile(new File("conf/application.conf")))))

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
  val claim = Json.obj("userId" -> UUID.fromString("00000000-0000-0000-0000-000000000001"))
  val expectedJWT = JwtJson.encode(claim, jWTParamsProvider.secretKey, jWTParamsProvider.algorithm)

  trait JWTChecker {
    def checkJWT(authentication:JsValue) =
      (contentFromRequest(authentication)  \ "token").asOpt[String] should contain(expectedJWT)
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

  private def contentFromRequest(postData:JsValue):JsValue =
    contentAsJson(route(app, FakeRequest(POST, "/authenticate")
               .withJsonBody(postData)
               .withHeaders(HeaderNames.CONTENT_TYPE -> "application/json"))
               .get)

}
