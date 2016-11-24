package access.authentication

import com.google.inject.Inject
import communication.LinkSender
import org.joda.time.Days
import play.api.Configuration
import user.UserMessage
import util.TimeProvider

import scala.util.{Random, Success}

class PasswordResetCodeSenderImpl @Inject()(
    authenticationAPI: AuthenticationAPI,
    linkSender: LinkSender,
    timeProvider: TimeProvider,
    configuration: Configuration)
  extends PasswordResetCodeSender {

  override def send(user: UserMessage, host: String): Unit = {
    authenticationAPI.retrievePasswordResetCode(user.email)
        .filter { passwordResetCodeAndDate =>
          Days.daysBetween(passwordResetCodeAndDate.date.withTimeAtStartOfDay(), timeProvider.now().withTimeAtStartOfDay()).getDays <
          configuration.getInt("crauth.passwordResetLinkIsValidForDays").getOrElse(10)
        }
    .fold[Unit] {
      val passwordResetCode = Random.alphanumeric.take(9).mkString.toLowerCase.replaceAll("0", "q").replaceAll("8", "p")
      authenticationAPI.storePasswordResetCode(user.email, passwordResetCode) match {
        case Success(retrievedUser) =>
          val passwordResetCodeWithDashes =
            Seq(passwordResetCode.take(3),  passwordResetCode.slice(3, 6), passwordResetCode.takeRight(3)).mkString("-")
          linkSender
          .send(retrievedUser, passwordResetCodeWithDashes, "passwordresetlink.subject", "passwordresetlink.body")
        case _ =>
      }
    } { existingPasswordResetCode =>
      val passwordResetCodeWithDashes =
        Seq(existingPasswordResetCode.code.take(3),
            existingPasswordResetCode.code.slice(3, 6),
            existingPasswordResetCode.code.takeRight(3)).mkString("-")
      linkSender
      .send(user, passwordResetCodeWithDashes, "passwordresetlink.subject", "passwordresetlink.body")
      }




  }

}
