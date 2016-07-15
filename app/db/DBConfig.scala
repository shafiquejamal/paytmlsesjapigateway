package db

trait DBConfig {

  def setUpAllDB(): Unit

  def closeAll(): Unit

  def dBName:String
  def driver:String
  def url:String
  def username:String
  def password:String

}
