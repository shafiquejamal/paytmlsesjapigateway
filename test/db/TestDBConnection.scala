package db

import java.io.File

import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterEach
import play.api.Configuration
import scalikejdbc.DBSession
import user.{UserFixture, WrappedResultSetToTestUserConverterImpl}
import util.PlayConfigParamsProvider

trait TestDBConnection extends InitialMigration { this: CrauthAutoRollback with BeforeAndAfterEach with UserFixture  =>

  val converter = new WrappedResultSetToTestUserConverterImpl()
  override val dBConfig =
    new ScalikeJDBCTestDBConfig(
      new PlayConfigParamsProvider(
        new Configuration(ConfigFactory.parseFile(new File("conf/application.conf")).resolve())))

  override def fixture(implicit session: DBSession) {
    migrate(dBConfig)
    sqlToAddUsers.foreach(_.update.apply())
  }

  override def beforeEach() {
    dBConfig.setUpAllDB()
  }

  override def afterEach() {
    dBConfig.closeAll()
  }

}
