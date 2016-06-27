package db

import org.flywaydb.core.Flyway
import scalikejdbc.DBSession
import scalikejdbc.scalatest.AutoRollback
import user.WrappedResultSetToTestUserConverterImpl

trait TestDBConnection { this: AutoRollback =>

  val converter = new WrappedResultSetToTestUserConverterImpl()
  val dBConfig = new ScalikeJDBCTestDBConfig()

  override def fixture(implicit session: DBSession) {
    val flyway = new Flyway()
    flyway.setDataSource("jdbc:h2:mem:play", "sa", "")
    flyway.migrate()
  }

}
