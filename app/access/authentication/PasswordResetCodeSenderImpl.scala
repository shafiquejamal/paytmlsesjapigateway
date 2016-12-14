package access.authentication

import access.registration.ActivationCodeGenerator
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

  val key = configuration.getString(ActivationCodeGenerator.configurationKey).getOrElse("")

  override def send(user: UserMessage, host: String): Unit = {
    authenticationAPI.retrievePasswordResetCode(user.email)
        .filter { passwordResetCodeAndDate =>
          Days.daysBetween(passwordResetCodeAndDate.date.withTimeAtStartOfDay(),
            timeProvider.now().withTimeAtStartOfDay()).getDays <
              configuration.getInt("crauth.passwordResetLinkIsValidForDays").getOrElse(10)
        }
    .fold[Unit] {
      val passwordResetCode = Random.alphanumeric.take(20).mkString
      val hashedPasswordResetCodeWithoutDashes =
        ActivationCodeGenerator.generate(passwordResetCode, key)
      val hashedPasswordResetCodeWithDashes = ActivationCodeGenerator.codeWithDashes(hashedPasswordResetCodeWithoutDashes)
      authenticationAPI.storePasswordResetCode(user.email, passwordResetCode) match {
        case Success(retrievedUser) =>
          linkSender
          .send(retrievedUser, hashedPasswordResetCodeWithDashes, "passwordresetlink.subject", "passwordresetlink.body")
        case _ =>
      }
    } { existingPasswordResetCode =>
      val hashedPasswordResetCodeToSend =
        ActivationCodeGenerator
        .generate(existingPasswordResetCode.code, key)
      linkSender
      .send(user, hashedPasswordResetCodeToSend, "passwordresetlink.subject", "passwordresetlink.body")
    }

  }

}
