package access.authentication

import com.google.inject.Inject
import communication.LinkSender
import org.joda.time.Days
import play.api.Configuration
import user.User
import util.TimeProvider

import scala.util.{Random, Success}

class PasswordResetCodeSenderImpl @Inject()(
    authenticationAPI: AuthenticationAPI,
    linkSender: LinkSender,
    timeProvider: TimeProvider,
    configuration: Configuration)
  extends PasswordResetCodeSender {

  override def send(user: User, host: String): Unit = {
    authenticationAPI.retrievePasswordResetCode(user.email)
        .filter { passwordResetCodeAndDate =>
          Days.daysBetween(passwordResetCodeAndDate.date.withTimeAtStartOfDay(), timeProvider.now().withTimeAtStartOfDay()).getDays <
          configuration.getInt("crauth.passwordResetLinkIsValidForDays").getOrElse(10)
                }
    .fold[Unit] {
      val passwordResetCode = Random.alphanumeric.take(50).mkString
      authenticationAPI.storePasswordResetCode(user.email, passwordResetCode) match {
        case Success(retrievedUser) =>
          linkSender
          .send(retrievedUser, host, passwordResetCode, "reset-password", "passwordresetlink.subject", "passwordresetlink.body")
        case _ =>
      }
    }(existingPasswordResetCode =>
      linkSender
        .send(user, host, existingPasswordResetCode.code, "reset-password", "passwordresetlink.subject", "passwordresetlink.body")
     )




  }

}
