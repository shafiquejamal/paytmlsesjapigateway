package access.registration

import java.util.UUID

import user.{User, UserStatus}

import scala.util.Try

trait RegistrationAPI {

  def signUp(registrationMessage: RegistrationMessage, statusOnRegistration: UserStatus): Try[User]

  def isUsernameIsAvailable(username: String): Boolean

  def isEmailIsAvailable(email: String): Boolean

  def activate(userId: UUID): Try[User]

}
