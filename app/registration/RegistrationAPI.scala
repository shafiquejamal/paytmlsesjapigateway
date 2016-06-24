package registration

import entity.User

import scala.util.Try

trait RegistrationAPI {

  def signUp(registrationMessage: RegistrationMessage):Try[User]

  def isUsernameIsAvailable(username:String):Boolean

  def isEmailIsAvailable(email:String):Boolean

}
