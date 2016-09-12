package user

import java.util.UUID

import access.authentication.PasswordResetCodeAndDate
import org.joda.time.DateTime

import scala.util.Try

trait UserDAO {

  def byUsername(username: String, userFilter: User => Boolean): Option[User]

  def byEmail(email: String, userFilter: User => Boolean): Option[User]

  def add(
      user: User,
      created: DateTime,
      uUID: UUID,
      userFilter: User => Boolean): Try[User]

  def by(id: UUID, userFilter: User => Boolean): Option[User]

  def changeUsername(
    id: UUID, newUsername:String,
    created:DateTime,
    userFilter: User => Boolean): Try[User]

  def changePassword(id: UUID, newHashedPassword:String, created:DateTime): Try[User]

  def addStatus(id: UUID, userStatus:UserStatus, created:DateTime): Try[User]

  def addPasswordResetCode(userId: UUID, passwordResetCode:String, created:DateTime, active:Boolean): Try[User]

  def passwordResetCode(userId: UUID): Option[PasswordResetCodeAndDate]

  def passwordResetCode(userId: UUID, code:String): Option[PasswordResetCodeAndDate]

  def allLogoutDate(id: UUID):Option[DateTime]

  def addAllLogoutDate(id: UUID, allLogoutDate: DateTime, created:DateTime):Try[User]

}
