package db

import org.scalatest.BeforeAndAfterEach
import scalikejdbc.DBSession
import user.{UserFixture, WrappedResultSetToTestUserConverterImpl}

trait TestDBConnection extends InitialMigration { this: CrauthAutoRollback with BeforeAndAfterEach with UserFixture  =>

  val converter = new WrappedResultSetToTestUserConverterImpl()
  override val dBConfig = new ScalikeJDBCTestDBConfig()

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
