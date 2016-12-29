package contact.phone

import access.{JWTParamsProvider, TestJWTParamsProviderImpl}
import db.{DBConfig, InitialMigration, OneAppPerTestWithOverrides, ScalikeJDBCTestDBConfig}
import org.scalatest._
import pdi.jwt.JwtJson
import play.api.inject._
import play.api.libs.json.Json
import scalikejdbc.NamedAutoSession
import user.UserFixture
import util._


class PhoneControllerATest
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
        bind[TimeProvider].to[TestTimeProviderImpl]
       )

  val dBConfig = new ScalikeJDBCTestDBConfig()

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
  val claim = Json.obj("userId" -> id1, "iat" -> timeProvider.now())
  val jWT = JwtJson.encode(claim, jWTParamsProvider.privateKey, jWTParamsProvider.algorithm)

}
