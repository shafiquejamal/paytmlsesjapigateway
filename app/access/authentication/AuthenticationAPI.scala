package access.authentication

import java.util.UUID

import user.UserMessage

import scala.util.Try

trait AuthenticationAPI {

  def userById(id: UUID): Option[UserMessage]

  def user(authenticationMessage: AuthenticationMessage): Option[UserMessage]

  def storePasswordResetCode(email: String, passwordResetCode: String): Try[UserMessage]

  def retrievePasswordResetCode(email: String): Option[PasswordResetCodeAndDate]

  def resetPassword(email: String, code: String, newPassword: String): Try[UserMessage]
  
}
