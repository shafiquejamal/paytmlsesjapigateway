package access.registration

import access.CodeSender
import com.google.inject.Inject
import com.typesafe.scalalogging.LazyLogging
import user.{UserMessage, UserStatus}

class AccountActivationCodeSenderImpl @Inject()(linkSender: CodeSender)
  extends AccountActivationCodeSender
  with LazyLogging {

  override def sendActivationCode(user: UserMessage, key: String): Unit = {
    val activationCodeWithDashes =
      ActivationCodeGenerator.generateWithDashes(user.maybeId.map(_.toString).getOrElse(""), key)
    logger.info(s"Activation code: $activationCodeWithDashes")
    linkSender.send(user, activationCodeWithDashes, "activation.subject", "activation.body")
  }

  override val statusOnRegistration = UserStatus.Unverified

}
