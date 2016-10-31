package access.authentication

import java.util.UUID

import org.joda.time.DateTime
import user.UserMessage

import scala.util.Try

trait AuthenticationAPI {

  def userById(id: UUID): Option[UserMessage]

  def validateOneTime(id: UUID, iat: DateTime): Option[UserMessage]

  def user(authenticationMessage: AuthenticationMessage): Option[UserMessage]

  def storePasswordResetCode(email: String, passwordResetCode: String): Try[UserMessage]

  def retrievePasswordResetCode(email: String): Option[PasswordResetCodeAndDate]

  def resetPassword(email: String, code: String, newPassword: String): Try[UserMessage]

  def allLogoutDate(id: UUID): Option[DateTime]

  def logoutAllDevices(id: UUID): Try[UserMessage]
}
