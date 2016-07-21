package access.registration

import user.{User, UserStatus}

class NoEmailVerificationAccountActivationLinkSenderImpl extends AccountActivationLinkSender {

  override def sendActivationCode(user: User, host: String, key: String): Unit = {}

  override val statusOnRegistration = UserStatus.Active

}
