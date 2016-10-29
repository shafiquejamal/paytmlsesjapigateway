package chat

import access.{JWTParamsProvider, TestJWTParamsProviderImpl}
import communication.{Emailer, TestEmailerImpl}
import db.{DBConfig, InitialMigration, OneAppPerTestWithOverrides, ScalikeJDBCTestDBConfig}
import org.scalatest._
import play.api.inject._
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
}
