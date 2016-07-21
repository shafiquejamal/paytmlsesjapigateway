package access.registration

import java.util.UUID

import com.google.inject.{Inject, Singleton}
import user.UserStatus.{Active, _}
import user.{User, UserDAO, UserStatus}
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

  override def signUp(registrationMessage: RegistrationMessage, statusOnRegistration: UserStatus):Try[User] =
      user.create(None,
        registrationMessage.maybeUsername.getOrElse(registrationMessage.email),
        registrationMessage.email,
        hash(registrationMessage.password),
        statusOnRegistration,
        Some(timeProvider.now()))
    .add(userDAO, uUIDProvider, registrationUserFilter, usernameAndEmailIsNotAvailableFilter)

  override def isUsernameIsAvailable(username:String): Boolean =
    userDAO.byUsername(username, usernameAndEmailIsNotAvailableFilter).isEmpty

  override def isEmailIsAvailable(email:String): Boolean =
    userDAO.byEmail(email, usernameAndEmailIsNotAvailableFilter).isEmpty

  override def activate(userId:UUID): Try[User] = userDAO.addStatus(userId, Active, timeProvider.now())

}
