package db

import java.io.File

import com.typesafe.config.ConfigFactory
import org.flywaydb.core.Flyway
import play.api.Configuration
import scalikejdbc.DBSession
import scalikejdbc.scalatest.AutoRollback
import user.WrappedResultSetToTestUserConverterImpl
import util.PlayConfigParamsProvider

trait TestDBConnection { this: AutoRollback =>

  val converter = new WrappedResultSetToTestUserConverterImpl()
  val dBConfig = new ScalikeJDBCTestDBConfig()
  val configParamsProvider =
    new PlayConfigParamsProvider(new Configuration(ConfigFactory.parseFile(new File("conf/application.conf"))))

  override def fixture(implicit session: DBSession) {
    val configParams = configParamsProvider.configParams
    val url = configParams.getOrElse("db.default.url", "")
    val username = configParams.getOrElse("db.default.username", "")
    val password = configParams.getOrElse("db.default.password", "")
    val flyway = new Flyway()
    flyway.setDataSource(url, username, password)
    flyway.migrate()
  }

}
