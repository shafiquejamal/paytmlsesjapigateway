package access.registration

import com.google.inject.Inject
import communication.LinkSender
import user.{UserMessage, UserStatus}

class AccountActivationLinkSenderImpl @Inject()(linkSender: LinkSender) extends AccountActivationLinkSender {

  override def sendActivationCode(user: UserMessage, host: String, key: String): Unit = {

    val activationCode = ActivationCodeGenerator.generate(user.maybeId.map(_.toString).getOrElse(""), key)
    val activationCodeWithDashes = ActivationCodeGenerator.codeWithDashes(activationCode)
    linkSender.send(user, activationCodeWithDashes, "activation.subject", "activation.body")
  }

  override val statusOnRegistration = UserStatus.Unverified

}
