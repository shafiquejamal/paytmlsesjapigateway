package access.authentication

import com.google.inject.Inject
import communication.LinkSender

import scala.util.{Random, Success}

class PasswordResetCodeSenderImpl @Inject()(
    authenticationAPI: AuthenticationAPI,
    linkSender: LinkSender)
  extends PasswordResetCodeSender {

  override def send(email: String, host: String): Unit = {
    val passwordResetCode = Random.alphanumeric.take(50).mkString

    authenticationAPI.storePasswordResetCode(email, passwordResetCode) match {
      case Success(user) =>
        linkSender
        .send(user, host, passwordResetCode, "reset-password", "passwordresetlink.subject", "passwordresetlink.body")
      case _ =>

    }

  }

}
