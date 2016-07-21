package access.authentication

import java.util.UUID

import user.User

import scala.util.Try

trait AuthenticationAPI {

  def userById(id: UUID): Option[User]

  def user(authenticationMessage: AuthenticationMessage): Option[User]

  def storePasswordResetCode(email: String, passwordResetCode: String): Try[User]
  
}
