package access.registration

import com.google.inject.Inject
import communication.LinkSender
import user.{UserMessage, UserStatus}

class AccountActivationLinkSenderImpl @Inject()(linkSender: LinkSender) extends AccountActivationLinkSender {

  override def sendActivationCode(user: UserMessage, host: String, key: String): Unit = {
    val activationCodeWithDashes =
      ActivationCodeGenerator.generateWithDashes(user.maybeId.map(_.toString).getOrElse(""), key)
    linkSender.send(user, activationCodeWithDashes, "activation.subject", "activation.body")
  }

  override val statusOnRegistration = UserStatus.Unverified

}
