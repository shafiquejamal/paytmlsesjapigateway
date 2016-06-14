package user

import java.util.UUID

import entity.User
import org.joda.time.DateTime

import scala.util.Try

trait UserDAO {

  def byUserName(userName:String):Option[User]

  def byEmail(email:String):Option[User]

  def addFirstTime(user:User, created:DateTime, uUID: UUID):Try[User]

}
