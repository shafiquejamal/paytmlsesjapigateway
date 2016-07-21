package access.authentication

trait PasswordResetCodeSender {

  def send(email: String, host: String): Unit

}
