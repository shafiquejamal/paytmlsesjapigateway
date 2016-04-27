package user

import entity.Active

case class User(
                 id:Option[String],
                 userName:Option[String],
                 emailAddress:Option[String],
                 password:String,
                 override val isActive:Boolean
               ) extends Active[User]

object User {
  def apply(id:String, password:String):User = User(Some(id), None, None, password, isActive = true)
  def apply(id:String, email:String, password:String):User = User(Some(id), None, Some(email), password, isActive = true)
  def apply(id:String, password:String, isActive:Boolean):User = User(Some(id), None, None, password, isActive)
  def apply(id:String, email:String, password:String, isActive:Boolean):User =
    User(Some(id), None, Some(email), password, isActive)
}
