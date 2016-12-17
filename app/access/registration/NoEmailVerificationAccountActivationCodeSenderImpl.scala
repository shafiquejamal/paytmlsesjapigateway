package access.registration

import user.{UserMessage, UserStatus}

class NoEmailVerificationAccountActivationCodeSenderImpl extends AccountActivationCodeSender {

  override def sendActivationCode(user: UserMessage, key: String): Unit = {}

  override val statusOnRegistration = UserStatus.Active

}
