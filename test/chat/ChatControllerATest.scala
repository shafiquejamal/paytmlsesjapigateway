package chat

import java.util.UUID

import access.{JWTParamsProvider, TestJWTParamsProviderImpl}
import communication.{Emailer, TestEmailerImpl}
import db.{DBConfig, InitialMigration, OneAppPerTestWithOverrides, ScalikeJDBCTestDBConfig}
import org.scalatest._
import pdi.jwt.JwtJson
import play.api.inject._
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import scalikejdbc.NamedAutoSession
import user.UserFixture
import util.{TestTimeProviderImpl, TestUUIDProviderImpl, TimeProvider, UUIDProvider}

class ChatControllerATest
  extends FlatSpec
  with ShouldMatchers
  with OneAppPerTestWithOverrides
  with BeforeAndAfterEach
  with InitialMigration
  with UserFixture {

  val dBConfig = new ScalikeJDBCTestDBConfig()

  override def overrideModules =
    Seq(
      bind[DBConfig].to[ScalikeJDBCTestDBConfig],
      bind[JWTParamsProvider].to[TestJWTParamsProviderImpl],
      bind[UUIDProvider].to[TestUUIDProviderImpl],
      bind[Emailer].to[TestEmailerImpl],
      bind[TimeProvider].to[TestTimeProviderImpl]
    )

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

  val timeProvider = new TestTimeProviderImpl()
  val jWTParamsProvider = new TestJWTParamsProviderImpl()
  val nonSingleUseClaim =
    Json.obj("userId" -> UUID.fromString("00000000-0000-0000-0000-000000000001"), "iat" -> timeProvider.now().minusMillis(5))
  val singleUseClaim =
    Json.obj("userId" -> UUID.fromString("00000000-0000-0000-0000-000000000001"),
             "iat" -> timeProvider.now(),
             "tokenUse" -> "single")
  val nonSingleUseJWT = JwtJson.encode(nonSingleUseClaim, jWTParamsProvider.secretKey, jWTParamsProvider.algorithm)
  val expectedJWT = JwtJson.encode(singleUseClaim, jWTParamsProvider.secretKey, jWTParamsProvider.algorithm)

  "Requesting a single use token" should "generate a single use token if the regular token is valid and not expired" in {
    val postData = Json.toJson(Map("token" -> nonSingleUseClaim))

    (contentFromRequest(postData) \ "singleUseToken").asOpt[String] should contain(expectedJWT)
  }

  private def contentFromRequest(postData:JsValue, path:String = "/single-use-token"):JsValue =
    contentAsJson(
      route(app, FakeRequest(POST, path)
      .withJsonBody(postData)
      .withHeaders(("Authorization", "Bearer " + nonSingleUseJWT))
      ).get
    )
}
