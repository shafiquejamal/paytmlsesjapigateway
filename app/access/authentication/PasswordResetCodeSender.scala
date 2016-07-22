package access.authentication

import user.User

trait PasswordResetCodeSender {

  def send(user: User, host: String): Unit

}
