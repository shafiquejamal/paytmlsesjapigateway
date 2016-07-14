package access

import access.authentication.AuthenticationAPI
import org.scalamock.scalatest.MockFactory
import org.scalatest._
import org.scalatestplus.play.OneAppPerTest
import pdi.jwt.{JwtAlgorithm, JwtJson}
import play.api.libs.json.Json
import play.api.mvc.Controller
import play.api.test.FakeRequest
import play.api.test.Helpers._
import user.TestUserImpl
import util.TestUUIDProviderImpl


class AuthenticatedActionATest extends FlatSpec with ShouldMatchers with OneAppPerTest with MockFactory {

  val secretKey = "some secret key"
  val algorithm = JwtAlgorithm.HS256
  val oKcontent = "request was ok"

  class ExampleController(api:AuthenticationAPI) extends Controller {
    def index = new AuthenticatedActionCreator(api, secretKey, algorithm).AuthenticatedAction {Ok(oKcontent) }
  }

  val mockedAuthenticationAPI = mock[AuthenticationAPI]

  val uUIDProvider = TestUUIDProviderImpl
  val uUUID = uUIDProvider.randomUUID()
  val controller = new ExampleController(mockedAuthenticationAPI)
  val claim = Json.obj("userId" -> uUUID)

  "The secured api" should "allow access if the claim is correct and the api returns a user" in {
    val user = new TestUserImpl().copy(maybeId = Some(uUUID))
    (mockedAuthenticationAPI.userById _ ).expects(uUUID).returning(Some(user))
    val token = JwtJson.encode(claim, secretKey, algorithm)
    val result = controller.index.apply(FakeRequest(GET, "/test").withHeaders(("token", token)) )

    status(result) shouldBe 200
    contentAsString(result) shouldBe oKcontent
  }

  it should "deny access if the api does not return a user" in {
    (mockedAuthenticationAPI.userById _ ).expects(uUUID).returning(None)
    val token = JwtJson.encode(claim, secretKey, algorithm)
    val result = controller.index.apply(FakeRequest(GET, "/test").withHeaders(("token", token)) )

    status(result) shouldBe 401
    contentAsString(result) shouldBe empty
  }

  it should "deny access if the token is not valid" in {
    val token = JwtJson.encode(claim, "wrong secret key", algorithm)
    val result = controller.index.apply(FakeRequest(GET, "/test").withHeaders(("token", token)) )

    status(result) shouldBe 401
    contentAsString(result) shouldBe empty
  }

  it should "deny access if the token is not present" in {
    val result = controller.index.apply(FakeRequest(GET, "/test") )

    status(result) shouldBe 401
    contentAsString(result) shouldBe empty
  }

}
