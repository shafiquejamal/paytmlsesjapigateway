package db

import com.google.inject.{Inject, Singleton}
import play.api.inject.ApplicationLifecycle
import scalikejdbc.config.DBs
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

  override def setUpAllDB(): Unit = setUp("db.default", configParams)
  override def closeAll(): Unit = DBs.closeAll()
  
  setUpAllDB()

  lifecycle.addStopHook { () =>
    Future.successful(closeAll())
  }

}