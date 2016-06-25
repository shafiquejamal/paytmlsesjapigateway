package db

import com.google.inject.{Inject, Singleton}
import play.api.inject.ApplicationLifecycle
import scalikejdbc.config.DBs

import scala.concurrent.Future

@Singleton
class ScalikeJDBCDevProdDBConfig @Inject()(lifecycle: ApplicationLifecycle) extends DBConfig {

  override def setUpAllDB(): Unit = DBs.setupAll()

  override def closeAll(): Unit = DBs.closeAll()

  setUpAllDB()

  lifecycle.addStopHook { () =>
    Future.successful(closeAll())
  }

}