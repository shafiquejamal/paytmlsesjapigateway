package registration

import com.google.inject.{Inject, Singleton}
import user.UserStatus.{Active, _}
import user.{User, UserDAO}
import util.Password.hash
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
