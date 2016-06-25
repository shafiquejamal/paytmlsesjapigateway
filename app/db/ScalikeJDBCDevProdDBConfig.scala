package db

import scalikejdbc.config.DBs

class ScalikeJDBCDevProdDBConfig extends DBConfig {

  override def setUpAllDB(): Unit = DBs.setupAll()

  override def closeAll(): Unit = DBs.closeAll()

}

object ScalikeJDBCDevProdDBConfig {

  def apply = new ScalikeJDBCDevProdDBConfig()

}