package access.registration

import com.google.inject.Inject
import communication.CodeSender
import user.{UserMessage, UserStatus}

class AccountActivationCodeSenderImpl @Inject()(linkSender: CodeSender) extends AccountActivationCodeSender {

  override def sendActivationCode(user: UserMessage, key: String): Unit = {
    val activationCodeWithDashes =
      ActivationCodeGenerator.generateWithDashes(user.maybeId.map(_.toString).getOrElse(""), key)
    linkSender.send(user, activationCodeWithDashes, "activation.subject", "activation.body")
  }

  override val statusOnRegistration = UserStatus.Unverified

}
