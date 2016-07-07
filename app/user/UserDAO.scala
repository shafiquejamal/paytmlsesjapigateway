package user

import java.util.UUID

import entity.User
import org.joda.time.DateTime

import scala.util.Try

trait UserDAO {

  def byUsername(username: String, userFilter: User => Boolean): Option[User]

  def byEmail(email: String, userFilter: User => Boolean): Option[User]

  def add(
    user: User,
    created: DateTime,
    uUID: UUID,
    registrationUserFilter: User => Boolean,
    authenticationUserFilter: User => Boolean): Try[User]

  def by(id: UUID, userFilter: User => Boolean): Option[User]

  def changeUsername(
    id: UUID, newUsername:String,
    created:DateTime,
    userFilter: User => Boolean): Try[User]

}
