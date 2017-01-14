package access.registration

import java.util.UUID

import com.eigenroute.id.UUIDProvider
import com.eigenroute.time.TimeProvider
import com.google.inject.{Inject, Singleton}
import org.apache.commons.validator.routines.EmailValidator
import user.UserStatus.{Active, _}
import user.{User, UserDAO, UserMessage, UserStatus}
import util.Password.hash

import scala.util.Try

@Singleton
class RegistrationFacade @Inject() (
    userDAO:UserDAO,
    user:User,
    timeProvider: TimeProvider,
    uUIDProvider: UUIDProvider)
  extends RegistrationAPI {

  override def signUp(registrationMessage: RegistrationMessage, statusOnRegistration: UserStatus):Try[UserMessage] =
      user.create(None,
        registrationMessage.maybeUsername.getOrElse(registrationMessage.email),
        registrationMessage.email,
        hash(registrationMessage.password),
        statusOnRegistration,
        Some(timeProvider.now()))
    .add(userDAO, uUIDProvider, registrationUserFilter, usernameAndEmailIsNotAvailableFilter)

  override def isUsernameIsAvailable(username:String): Boolean =
    userDAO.byUsername(username, usernameAndEmailIsNotAvailableFilter).isEmpty

  override def isEmailIsAvailable(email:String): Boolean = {
    !EmailValidator.getInstance().isValid(email) || userDAO.byEmail(email, usernameAndEmailIsNotAvailableFilter).isEmpty
  }

  override def activate(userId:UUID): Try[UserMessage] = userDAO.addStatus(userId, Active, timeProvider.now())

}
