package db

import scalikejdbc.config.DBs

class ScalikeJDBCTestDBConfig extends DBConfig {

  override def setUpAllDB(): Unit = DBs.setupAll()

  override def closeAll(): Unit = DBs.closeAll()

}

object ScalikeJDBCTestDBConfig {

  def apply() = new ScalikeJDBCTestDBConfig()

}