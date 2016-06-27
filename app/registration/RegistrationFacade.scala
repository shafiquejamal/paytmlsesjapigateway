package registration

import com.google.inject.{Inject, Singleton}
import entity.User
import registration.PasswordHasher.hash
import user.UserDAO
import user.UserStatus.{Active, _}
import util.{TimeProvider, UUIDProvider}

import scala.util.Try

@Singleton
class RegistrationFacade @Inject() (
    userDAO:UserDAO,
    user:User,
    timeProvider: TimeProvider,
    uUIDProvider: UUIDProvider)
  extends RegistrationAPI {

  override def signUp(registrationMessage: RegistrationMessage):Try[User] =
    user.create(None,
        registrationMessage.maybeUsername.getOrElse(registrationMessage.email),
        registrationMessage.email,
        hash(registrationMessage.password),
        Active,
        Some(timeProvider.now()))
      .add(userDAO, uUIDProvider, registrationUserFilter, authenticationUserFilter)

  override def isUsernameIsAvailable(username:String): Boolean =
    userDAO.byUsername(username, usernameAndEmailIsNotAvailableFilter).isEmpty

  override def isEmailIsAvailable(email:String): Boolean =
    userDAO.byEmail(email, usernameAndEmailIsNotAvailableFilter).isEmpty

}
