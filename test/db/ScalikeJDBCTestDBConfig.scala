package db

import java.io.File

import com.google.inject.Inject
import com.typesafe.config.ConfigFactory
import play.api.Configuration
import scalikejdbc.config.DBs
import util.{ConfigParamsProvider, PlayConfigParamsProvider}

class ScalikeJDBCTestDBConfig @Inject() (configParamsProvider: ConfigParamsProvider) extends DBConfig {

  val configParams = configParamsProvider.configParams
  override val driver = configParams.getOrElse("db.test.driver", "")
  override val url = configParams.getOrElse("db.test.url", "")
  override val dBName = url.substring(url.lastIndexOf("/") + 1)
  override val username = configParams.getOrElse("db.test.username", "")
  override val password = configParams.getOrElse("db.test.password", "")

  override def setUpAllDB(): Unit = setUp("db.test", configParams)

  override def closeAll(): Unit = DBs.closeAll()

}

object ScalikeJDBCTestDBConfig {

  def apply() = new ScalikeJDBCTestDBConfig(new PlayConfigParamsProvider(new Configuration(ConfigFactory.parseFile(new File("conf/application.conf")))))

}