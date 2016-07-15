package db

import java.io.File

import com.google.inject.Inject
import com.typesafe.config.ConfigFactory
import play.api.Configuration
import scalikejdbc.config.DBs
import scalikejdbc.{ConnectionPool, ConnectionPoolSettings}
import util.{ConfigParamsProvider, PlayConfigParamsProvider}

class ScalikeJDBCTestDBConfig @Inject() (configParamsProvider: ConfigParamsProvider) extends DBConfig {

  val configParams = configParamsProvider.configParams
  override val driver = configParams.getOrElse("db.test.driver", "")
  override val url = configParams.getOrElse("db.test.url", "")
  override val dBName = url.substring(url.lastIndexOf("/") + 1)
  override val username = configParams.getOrElse("db.test.username", "")
  override val password = configParams.getOrElse("db.test.password", "")

  override def setUpAllDB(): Unit = {
    val cpSettings = ConnectionPoolSettings(
      initialSize = configParams.get("db.test.poolInitialSize").map(_.toInt).getOrElse(10),
      maxSize = configParams.get("db.test.poolMaxSize").map(_.toInt).getOrElse(20),
      connectionTimeoutMillis = configParams.get("db.test.poolMaxSize").map(_.toLong).getOrElse(1000),
      validationQuery = configParams.getOrElse("db.test.poolValidationQuery", "select 1 as one"),
      connectionPoolFactoryName = configParams.getOrElse("db.test.poolFactoryName", ""),
      driverName = driver,
      warmUpTime = configParams.get("db.test.poolWarmUpTimeMillis").map(_.toLong).getOrElse(1000),
      timeZone = configParams.get("db.test.timeZone").orNull[String]
   )

    Class.forName(driver)
    ConnectionPool.add(Symbol(dBName), url, username, password, cpSettings)
  }

  override def closeAll(): Unit = DBs.closeAll()

}

object ScalikeJDBCTestDBConfig {

  def apply() = new ScalikeJDBCTestDBConfig(new PlayConfigParamsProvider(new Configuration(ConfigFactory.parseFile(new File("conf/application.conf")))))

}