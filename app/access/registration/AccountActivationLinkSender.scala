package access.registration

import user.{User, UserStatus}

trait AccountActivationLinkSender {

  def sendActivationCode(user: User, protocolAndHost: String, key: String): Unit

  def statusOnRegistration: UserStatus

}
