package access.registration

import user.{UserMessage, UserStatus}

trait AccountActivationCodeSender {

  def sendActivationCode(user: UserMessage, key: String): Unit

  def statusOnRegistration: UserStatus

}
