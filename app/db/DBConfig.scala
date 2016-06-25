package db

trait DBConfig {

  def setUpAllDB(): Unit

  def closeAll(): Unit

}
