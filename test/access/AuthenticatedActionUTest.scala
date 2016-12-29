package access

import java.io.File
import java.security.spec.ECPrivateKeySpec
import java.security.{KeyFactory, Security, PrivateKey}

import access.authentication.AuthenticationAPI
import com.google.inject.Inject
import com.typesafe.config.ConfigFactory
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.spec.ECNamedCurveSpec
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
  val uUID = uUIDProvider.randomUUID()
  val configuration = new Configuration(ConfigFactory.parseFile(new File("conf/application.test.conf")).resolve())
  val controller =
    new ExampleController(mockedAuthenticationAPI, jWTParamsProvider, configuration, new TestTimeProviderImpl())
  val timeProvider = new TestTimeProviderImpl
  val now = timeProvider.now()
  val claimNotExpired = Json.obj("userId" -> uUID, "iat" -> now.minusDays(1))
  val user = new TestUserImpl().copy(maybeId = Some(uUID))

  trait WrongSecretKey {
    val S = BigInt("abcd", 16)
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    val curveParams = ECNamedCurveTable.getParameterSpec("P-521")
    val curveSpec =
      new ECNamedCurveSpec("P-521", curveParams.getCurve, curveParams.getG, curveParams.getN, curveParams.getH)
    val privateSpec = new ECPrivateKeySpec(S.underlying(), curveSpec)
    val wrongSecretKey: PrivateKey = KeyFactory.getInstance("ECDSA", "BC").generatePrivate(privateSpec)
  }

  "The secured api" should "allow access if the claim is correct, the token has not expired, and the api returns a user" in {
    (mockedAuthenticationAPI.allLogoutDate _).expects(uUID).returning(None)
    (mockedAuthenticationAPI.userById _ ).expects(uUID).returning(Some(user))
    val token = JwtJson.encode(claimNotExpired, jWTParamsProvider.privateKey, jWTParamsProvider.algorithm)
    val result = controller.index.apply(FakeRequest(GET, "/test").withHeaders(("Authorization", "Bearer " + token)) )

    status(result) shouldBe 200
    contentAsString(result) shouldBe oKcontent
  }

  it should "deny access if the api does not return a user" in {
    (mockedAuthenticationAPI.allLogoutDate _).expects(uUID).returning(None)
    (mockedAuthenticationAPI.userById _ ).expects(uUID).returning(None)
    val token = JwtJson.encode(claimNotExpired, jWTParamsProvider.privateKey, jWTParamsProvider.algorithm)
    val result = controller.index.apply(FakeRequest(GET, "/test").withHeaders(("Authorization", "Bearer " + token)) )

    status(result) shouldBe 401
    contentAsString(result) shouldBe empty
  }

  it should "deny access if the token is not valid" in new WrongSecretKey {
    val token = JwtJson.encode(claimNotExpired, wrongSecretKey, jWTParamsProvider.algorithm)
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
    val token = JwtJson.encode(claimNotExpired, jWTParamsProvider.privateKey, jWTParamsProvider.algorithm)
    val result = controller.index.apply(FakeRequest(GET, "/test").withHeaders(("Authorization", token)) )

    status(result) shouldBe 401
    contentAsString(result) shouldBe empty
  }

  it should "deny access if the claim represents a valid user, but the token has expired" in {
    val claimExpired = Json.obj("userId" -> uUID, "iat" -> timeProvider.now().minusDays(3))
    val token = JwtJson.encode(claimExpired, jWTParamsProvider.privateKey, jWTParamsProvider.algorithm)
    val result = controller.index.apply(FakeRequest(GET, "/test").withHeaders(("Authorization", "Bearer " + token)) )

    status(result) shouldBe 401
    contentAsString(result) shouldBe empty
  }

  it should "deny access if the token is issued before the last allLogout date" in {
    (mockedAuthenticationAPI.allLogoutDate _).expects(uUID).returning(Some(now))
    val token = JwtJson.encode(claimNotExpired, jWTParamsProvider.privateKey, jWTParamsProvider.algorithm)
    val result = controller.index.apply(FakeRequest(GET, "/test").withHeaders(("Authorization", "Bearer " + token)) )

    status(result) shouldBe 401
    contentAsString(result) shouldBe empty
  }

  "Decoding and validating the token" should "return the unauthorized block if the api does not return a user" in {
    (mockedAuthenticationAPI.allLogoutDate _).expects(uUID).returning(None)
    (mockedAuthenticationAPI.userById _ ).expects(uUID).returning(None)
    val token = JwtJson.encode(claimNotExpired, jWTParamsProvider.privateKey, jWTParamsProvider.algorithm)

    controller
    .decodeAndValidateToken[String](token, (_, _) => "success", "failure", AllowedTokens(MultiUse)) shouldEqual "failure"
  }

  it should "return the success block if the claim is correct, the token has not expired, and the api returns a user" in {
    (mockedAuthenticationAPI.allLogoutDate _).expects(uUID).returning(None)
    (mockedAuthenticationAPI.userById _ ).expects(uUID).returning(Some(user))
    val token = JwtJson.encode(claimNotExpired, jWTParamsProvider.privateKey, jWTParamsProvider.algorithm)

    controller
    .decodeAndValidateToken[String](token, (_, _) => "success", "failure", AllowedTokens(MultiUse)) shouldEqual "success"
  }

  it should "return the unauthorized block if the token is a single use token, and MultiUse only is specified" in {
    (mockedAuthenticationAPI.allLogoutDate _).expects(uUID).returning(None)
    val singleUseClaim = Json.obj("userId" -> uUID, "iat" -> now.minusDays(1), "tokenUse" -> "single")
    val token = JwtJson.encode(singleUseClaim, jWTParamsProvider.privateKey, jWTParamsProvider.algorithm)

    controller
    .decodeAndValidateToken[String](token, (_, _) => "success", "failure", AllowedTokens(MultiUse)) shouldEqual "failure"
  }

  it should "return the authorized block if the token is a single use token, and SingleUse only is specified" in {
    val iat = now.minusDays(1)
    (mockedAuthenticationAPI.allLogoutDate _).expects(uUID).returning(None)
    (mockedAuthenticationAPI.validateOneTime _).expects(uUID, iat).returning(Some(user))
    val singleUseClaim = Json.obj("userId" -> uUID, "iat" -> iat, "tokenUse" -> "single")
    val token = JwtJson.encode(singleUseClaim, jWTParamsProvider.privateKey, jWTParamsProvider.algorithm)

    controller
    .decodeAndValidateToken[String](token, (_, _) => "success", "failure", AllowedTokens(SingleUse)) shouldEqual "success"
  }

  it should "return the authorized block if the token is a single use token, and SingleUse or MultiUser is specified - " +
  "single use token provided" in {
    val iat = now.minusDays(1)
    (mockedAuthenticationAPI.allLogoutDate _).expects(uUID).returning(None)
    (mockedAuthenticationAPI.validateOneTime _).expects(uUID, iat).returning(Some(user))
    val singleUseClaim = Json.obj("userId" -> uUID, "iat" -> iat, "tokenUse" -> "single")
    val token = JwtJson.encode(singleUseClaim, jWTParamsProvider.privateKey, jWTParamsProvider.algorithm)

    controller
    .decodeAndValidateToken[String](
      token, (_, _) => "success", "failure", AllowedTokens(Vector(SingleUse, MultiUse))) shouldEqual "success"
  }

  it should "return the authorized block if the token is a single use token, and SingleUse or MultiUser is specified - " +
  "multi use token provided" in {
    val iat = now.minusDays(1)
    (mockedAuthenticationAPI.allLogoutDate _).expects(uUID).returning(None)
    (mockedAuthenticationAPI.userById _).expects(uUID).returning(Some(user))
    val singleUseClaim = Json.obj("userId" -> uUID, "iat" -> iat)
    val token = JwtJson.encode(singleUseClaim, jWTParamsProvider.privateKey, jWTParamsProvider.algorithm)

    controller.decodeAndValidateToken[String](
      token, (_, _) => "success", "failure", AllowedTokens(Vector(SingleUse, MultiUse))) shouldEqual "success"
  }

  it should "return the unauthorized block if the token is not valid" in new WrongSecretKey {
    val token = JwtJson.encode(claimNotExpired, wrongSecretKey, jWTParamsProvider.algorithm)

    controller.decodeAndValidateToken[String](token, (_, _) => "success", "failure", null) shouldEqual "failure"
  }

  it should "return the unauthorized block if the claim represents a valid user, but the token has expired" in {
    val claimExpired = Json.obj("userId" -> uUID, "iat" -> timeProvider.now().minusDays(3))
    val token = JwtJson.encode(claimExpired, jWTParamsProvider.privateKey, jWTParamsProvider.algorithm)

    controller.decodeAndValidateToken[String](token, (_, _) => "success", "failure", null) shouldEqual "failure"
  }

  it should "return the unauthorized block if the token is issued before the last allLogout date" in {
    (mockedAuthenticationAPI.allLogoutDate _).expects(uUID).returning(Some(now))
    val token = JwtJson.encode(claimNotExpired, jWTParamsProvider.privateKey, jWTParamsProvider.algorithm)

    controller.decodeAndValidateToken[String](token, (_, _) => "success", "failure", null) shouldEqual "failure"
  }

}
