package access.registration

import user.{UserMessage, UserStatus}

class NoEmailVerificationAccountActivationLinkSenderImpl extends AccountActivationLinkSender {

  override def sendActivationCode(user: UserMessage, host: String, key: String): Unit = {}

  override val statusOnRegistration = UserStatus.Active

}
