package access

import access.authentication.AuthenticationAPI
import com.google.inject.Inject
import org.scalamock.scalatest.MockFactory
import org.scalatest._
import org.scalatestplus.play.OneAppPerTest
import pdi.jwt.JwtJson
import play.api.libs.json.Json
import play.api.mvc.Controller
import play.api.test.FakeRequest
import play.api.test.Helpers._
import user.TestUserImpl
import util.TestUUIDProviderImpl


class AuthenticatedActionATest extends FlatSpec with ShouldMatchers with OneAppPerTest with MockFactory {

  val oKcontent = "request was ok"
  val jWTParamsProvider = new TestJWTParamsProviderImpl()

  class ExampleController @Inject() (
      override val authenticationAPI:AuthenticationAPI,
      override val jWTParamsProvider: JWTParamsProvider)
    extends Controller
    with AuthenticatedActionCreator {

    def index = AuthenticatedAction { Ok(oKcontent) }
  }

  val mockedAuthenticationAPI = mock[AuthenticationAPI]

  val uUIDProvider = TestUUIDProviderImpl
  val uUUID = uUIDProvider.randomUUID()
  val controller = new ExampleController(mockedAuthenticationAPI, jWTParamsProvider)
  val claim = Json.obj("userId" -> uUUID)

  "The secured api" should "allow access if the claim is correct and the api returns a user" in {
    val user = new TestUserImpl().copy(maybeId = Some(uUUID))
    (mockedAuthenticationAPI.userById _ ).expects(uUUID).returning(Some(user))
    val token = JwtJson.encode(claim, jWTParamsProvider.secretKey, jWTParamsProvider.algorithm)
    val result = controller.index.apply(FakeRequest(GET, "/test").withHeaders(("Authorization", token)) )

    status(result) shouldBe 200
    contentAsString(result) shouldBe oKcontent
  }

  it should "deny access if the api does not return a user" in {
    (mockedAuthenticationAPI.userById _ ).expects(uUUID).returning(None)
    val token = JwtJson.encode(claim, jWTParamsProvider.secretKey, jWTParamsProvider.algorithm)
    val result = controller.index.apply(FakeRequest(GET, "/test").withHeaders(("Authorization", token)) )

    status(result) shouldBe 401
    contentAsString(result) shouldBe empty
  }

  it should "deny access if the token is not valid" in {
    val token = JwtJson.encode(claim, "wrong secret key", jWTParamsProvider.algorithm)
    val result = controller.index.apply(FakeRequest(GET, "/test").withHeaders(("Authorization", token)) )

    status(result) shouldBe 401
    contentAsString(result) shouldBe empty
  }

  it should "deny access if the token is not present" in {
    val result = controller.index.apply(FakeRequest(GET, "/test") )

    status(result) shouldBe 401
    contentAsString(result) shouldBe empty
  }

}
