package access.registration

import user.{User, UserStatus}

class NoEmailVerificationAccountActivatorImpl extends AccountActivator {

  override def sendActivationCode(user: User, protocolAndHost: String, key: String): Unit = {}

  override val statusOnRegistration = UserStatus.Active
  
}
