package access.registration

import java.io.File

import access.{JWTParamsProvider, TestJWTParamsProviderImpl}
import com.typesafe.config.ConfigFactory
import communication.{Emailer, TestEmailerImpl}
import db._
import org.scalatest._
import play.api.Configuration
import play.api.http.HeaderNames
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._
import scalikejdbc.NamedAutoSession
import user.UserFixture
import util._

class RegistrationControllerATest
  extends FlatSpec
  with ShouldMatchers
  with OneAppPerTestWithOverrides
  with BeforeAndAfterEach
  with InitialMigration
  with UserFixture {

  override def overrideModules =
    Seq(bind[DBConfig].to[ScalikeJDBCTestDBConfig],
        bind[JWTParamsProvider].to[TestJWTParamsProviderImpl],
        bind[UUIDProvider].to[TestUUIDProviderImpl],
        bind[Emailer].to[TestEmailerImpl],
        bind[TimeProvider].to[TestTimeProviderImpl]
        )

  val configParamsProvider =
    new PlayConfigParamsProvider(new Configuration(ConfigFactory.parseFile(new File("conf/application.conf")).resolve()))
  val dBConfig = new ScalikeJDBCTestDBConfig(configParamsProvider)
  val md5key = configParamsProvider.configParams(ActivationCodeGenerator.configurationKey)

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

  "Checking whether a username is available" should "return true if the username is available" in {
    val result = route(app, FakeRequest(GET, "/username/available")).get
    val content = contentAsJson(result)
    (content \ "status").asOpt[Boolean] should contain(true)
  }

  "Checking whether an email address is available" should "return true if the email is available" in {
    val result = route(app, FakeRequest(GET, "/email/available")).get
    val content = contentAsJson(result)
    (content \ "status").asOpt[Boolean] should contain(true)
  }

  "Registering a new user" should "result in success if the username and email address are available and these " +
  "and the password are valid" in {
    val registration = Json.toJson(Map("username" -> "newuser", "email" -> "spam@eigenroute.com", "password" -> "pass"))
    val result = route(app, FakeRequest(POST, "/register")
      .withJsonBody(registration)
      .withHeaders(HeaderNames.CONTENT_TYPE -> "application/json"))
      .get
    val content = contentAsJson(result)

    (content \ "status").asOpt[String] should contain("success")

    val checkEmailAvailable = contentAsJson(route(app, FakeRequest(GET, "/email/spam@eigenroute.com")).get)
    (checkEmailAvailable \ "status").asOpt[Boolean] should contain(false)

    val checkUsernameAvailable = contentAsJson(route(app, FakeRequest(GET, "/username/newuser")).get)
    (checkUsernameAvailable \ "status").asOpt[Boolean] should contain(false)
  }

  "Activating a new user" should "fail if the email does not represent a user in the db" in {
    val result = route(app, FakeRequest(POST, "/activate")
      .withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")
      .withJsonBody(Json.obj("email" -> "non@matching.com", "code" -> "non-matching-code")))
      .get
    status(result) shouldEqual BAD_REQUEST
  }

  it should "fail if the email and code combination is not valid" in {
    val wrongCode = ActivationCodeGenerator.generate(id7.toString, md5key)
    val result = route(app, FakeRequest(POST, s"/activate")
      .withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")
      .withJsonBody(Json.obj("email" -> "charlie@charlie.com", "code" -> wrongCode)))
      .get
    status(result) shouldEqual BAD_REQUEST
  }

  it should "fail if the user is blocked" in {
    val code = ActivationCodeGenerator.generate(id7.toString, md5key)
    val result = route(app, FakeRequest(POST, s"/activate")
      .withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")
      .withJsonBody(Json.obj("email" -> "diane@diane.com", "code" -> code)))
      .get
    (contentAsJson(result) \ "error").asOpt[String] should contain("this user is blocked")
  }

  it should "succeed if the user is admin" in {
    val code = ActivationCodeGenerator.generate(id3.toString, md5key)
    val result = route(app, FakeRequest(POST, s"/activate")
      .withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")
      .withJsonBody(Json.obj("email" -> "bob@bob.com", "code" -> code)))
      .get
    (contentAsJson(result) \ "error").asOpt[String] should contain("this user is already active")
  }

  it should "succeed if the user is active" in {
    val code = ActivationCodeGenerator.generate(id1.toString, md5key)
    val result = route(app, FakeRequest(POST, s"/activate")
      .withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")
      .withJsonBody(Json.obj("email" -> "alice@alice.com", "code" -> code)))
      .get
    (contentAsJson(result) \ "error").asOpt[String] should contain("this user is already active")
  }

  it should "succeed if the user is unverified and the code matches the email" in {
    val code = ActivationCodeGenerator.generate(id4.toString, md5key)
    val result = route(app, FakeRequest(POST, s"/activate?email=charlie%40charlie.com&code=$code")
      .withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")
      .withJsonBody(Json.obj("email" -> "charlie@charlie.com", "code" -> code)))
      .get
    (contentAsJson(result) \ "status").asOpt[String] should contain("success")
  }

  "resending an activation link" should "succeed if the message is well formed" in {
    val result = route(app, FakeRequest(POST, "/resend-activation-link")
      .withJsonBody(Json.obj("email"->"spam@eigenroute.com"))
      .withHeaders(HeaderNames.CONTENT_TYPE -> "application/json"))
      .get
    status(result) shouldBe OK
  }

  it should "fail if the message is badly formed" in {
    val result = route(app, FakeRequest(POST, "/resend-activation-link")
      .withJsonBody(Json.obj("bad"->"spam@eigenroute.com"))
      .withHeaders(HeaderNames.CONTENT_TYPE -> "application/json"))
      .get
    status(result) shouldBe BAD_REQUEST
  }

}
