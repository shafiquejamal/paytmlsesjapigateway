package user

import scala.util.Try

trait UserDAO {

  def byUserName(userName:String):Option[User]

  def byEmail(email:String):Option[User]

  def add(user:User):Try[User]

}
