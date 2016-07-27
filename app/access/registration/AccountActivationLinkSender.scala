package access.registration

import user.{UserMessage, UserStatus}

trait AccountActivationLinkSender {

  def sendActivationCode(user: UserMessage, host: String, key: String): Unit

  def statusOnRegistration: UserStatus

}
