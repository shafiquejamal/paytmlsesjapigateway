package user

import java.util.UUID

import entity.User
import org.joda.time.DateTime

import scala.util.Try

trait UserDAO {

  def byUsername(username: String): Option[User]

  def byEmail(email: String): Option[User]

  def addFirstTime(user: User, created: DateTime, uUID: UUID): Try[User]

  def by(id: UUID): Option[User]

  def byEmail(email: String, hashedPassword: String): Option[User]

  def byUsername(username: String, hashedPassword: String): Option[User]

}
