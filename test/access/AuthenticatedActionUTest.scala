package access

import java.io.File

import access.authentication.AuthenticationAPI
import com.google.inject.Inject
import com.typesafe.config.ConfigFactory
import org.scalamock.scalatest.MockFactory
import org.scalatest._
import org.scalatestplus.play.OneAppPerTest
import pdi.jwt.JwtJson
import play.Configuration
import play.api.libs.json.Json
import play.api.mvc.Controller
import play.api.test.FakeRequest
import play.api.test.Helpers._
import user.TestUserImpl
import util.{TestTimeProviderImpl, TestUUIDProviderImpl, TimeProvider}


class AuthenticatedActionUTest extends FlatSpec with ShouldMatchers with OneAppPerTest with MockFactory {

  val oKcontent = "request was ok"
  val jWTParamsProvider = new TestJWTParamsProviderImpl()

  class ExampleController @Inject() (
      override val authenticationAPI:AuthenticationAPI,
      override val jWTParamsProvider: JWTParamsProvider,
      override val configuration: Configuration,
      override val timeProvider: TimeProvider)
    extends Controller
    with AuthenticatedActionCreator {

    def index = AuthenticatedAction { Ok(oKcontent) }
  }

  val mockedAuthenticationAPI = mock[AuthenticationAPI]

  val uUIDProvider = new TestUUIDProviderImpl()
  val uUUID = uUIDProvider.randomUUID()
  val configuration = new Configuration(ConfigFactory.parseFile(new File("conf/application.conf")).resolve())
  val controller =
    new ExampleController(mockedAuthenticationAPI, jWTParamsProvider, configuration, new TestTimeProviderImpl())
  val timeProvider = new TestTimeProviderImpl
  val now = timeProvider.now()
  val claimNotExpired = Json.obj("userId" -> uUUID, "iat" -> now.minusDays(1))
  val user = new TestUserImpl().copy(maybeId = Some(uUUID))


  "The secured api" should "allow access if the claim is correct, the token has not expired, and the api returns a user" in {
    (mockedAuthenticationAPI.allLogoutDate _).expects(uUUID).returning(None)
    (mockedAuthenticationAPI.userById _ ).expects(uUUID).returning(Some(user))
    val token = JwtJson.encode(claimNotExpired, jWTParamsProvider.secretKey, jWTParamsProvider.algorithm)
    val result = controller.index.apply(FakeRequest(GET, "/test").withHeaders(("Authorization", "Bearer " + token)) )

    status(result) shouldBe 200
    contentAsString(result) shouldBe oKcontent
  }

  it should "deny access if the api does not return a user" in {
    (mockedAuthenticationAPI.allLogoutDate _).expects(uUUID).returning(None)
    (mockedAuthenticationAPI.userById _ ).expects(uUUID).returning(None)
    val token = JwtJson.encode(claimNotExpired, jWTParamsProvider.secretKey, jWTParamsProvider.algorithm)
    val result = controller.index.apply(FakeRequest(GET, "/test").withHeaders(("Authorization", "Bearer " + token)) )

    status(result) shouldBe 401
    contentAsString(result) shouldBe empty
  }

  it should "deny access if the token is not valid" in {
    val token = JwtJson.encode(claimNotExpired, "wrong secret key", jWTParamsProvider.algorithm)
    val result = controller.index.apply(FakeRequest(GET, "/test").withHeaders(("Authorization", "Bearer " + token)) )

    status(result) shouldBe 401
    contentAsString(result) shouldBe empty
  }

  it should "deny access if the token is not present" in {
    val result = controller.index.apply(FakeRequest(GET, "/test") )

    status(result) shouldBe 401
    contentAsString(result) shouldBe empty
  }

  it should "deny access if the authorization type is not present" in {
    val token = JwtJson.encode(claimNotExpired, jWTParamsProvider.secretKey, jWTParamsProvider.algorithm)
    val result = controller.index.apply(FakeRequest(GET, "/test").withHeaders(("Authorization", token)) )

    status(result) shouldBe 401
    contentAsString(result) shouldBe empty
  }

  it should "deny access if the claim represents a valid user, but the token has expired" in {
    val claimExpired = Json.obj("userId" -> uUUID, "iat" -> timeProvider.now().minusDays(3))
    val token = JwtJson.encode(claimExpired, jWTParamsProvider.secretKey, jWTParamsProvider.algorithm)
    val result = controller.index.apply(FakeRequest(GET, "/test").withHeaders(("Authorization", "Bearer " + token)) )

    status(result) shouldBe 401
    contentAsString(result) shouldBe empty
  }

  it should "deny access if the token is issued before the last allLogout date" in {
    (mockedAuthenticationAPI.allLogoutDate _).expects(uUUID).returning(Some(now))
    val token = JwtJson.encode(claimNotExpired, jWTParamsProvider.secretKey, jWTParamsProvider.algorithm)
    val result = controller.index.apply(FakeRequest(GET, "/test").withHeaders(("Authorization", "Bearer " + token)) )

    status(result) shouldBe 401
    contentAsString(result) shouldBe empty
  }

}
