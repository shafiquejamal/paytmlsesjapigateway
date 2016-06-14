package registration

import entity.User
import user.UserMessage

import scala.util.Try

trait RegistrationAPI {

  def signUp(userMessage:UserMessage, hashedPassword:String):Try[User]

}
