package db

import com.google.inject.{Inject, Singleton}
import play.api.inject.ApplicationLifecycle
import scalikejdbc.config.DBs
import scalikejdbc.{ConnectionPool, ConnectionPoolSettings}
import util.ConfigParamsProvider

import scala.concurrent.Future

@Singleton
class ScalikeJDBCDevProdDBConfig @Inject()(lifecycle: ApplicationLifecycle, configParamsProvider: ConfigParamsProvider)
  extends DBConfig {

  val configParams = configParamsProvider.configParams
  override val driver = configParams.getOrElse("db.default.driver", "")
  override val url = configParams.getOrElse("db.default.url", "")
  override val dBName = url.substring(url.lastIndexOf("/") + 1)
  override val username = configParams.getOrElse("db.default.username", "")
  override val password = configParams.getOrElse("db.default.password", "")

  override def setUpAllDB(): Unit = {
    val cpSettings = ConnectionPoolSettings(
      initialSize = configParams.get("db.default.poolInitialSize").map(_.toInt).getOrElse(10),
      maxSize = configParams.get("db.default.poolMaxSize").map(_.toInt).getOrElse(20),
      connectionTimeoutMillis = configParams.get("db.default.poolMaxSize").map(_.toLong).getOrElse(1000),
      validationQuery = configParams.getOrElse("db.default.poolValidationQuery", "select 1 as one"),
      connectionPoolFactoryName = configParams.getOrElse("db.default.poolFactoryName", ""),
      driverName = driver,
      warmUpTime = configParams.get("db.default.poolWarmUpTimeMillis").map(_.toLong).getOrElse(1000),
      timeZone = configParams.get("db.default.timeZone").orNull[String]
    )
  
    Class.forName(driver)
    ConnectionPool.add(Symbol(dBName), url, username, password, cpSettings)
  }

  override def closeAll(): Unit = DBs.closeAll()
  
  setUpAllDB()

  lifecycle.addStopHook { () =>
    Future.successful(closeAll())
  }

}