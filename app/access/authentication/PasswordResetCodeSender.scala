package access.authentication

import user.UserMessage

trait PasswordResetCodeSender {

  def send(user: UserMessage, host: String): Unit

}
