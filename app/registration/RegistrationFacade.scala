package registration

import com.google.inject.{Inject, Singleton}
import entity.User
import org.mindrot.jbcrypt.BCrypt
import user.UserDAO
import util.{TimeProvider, UUIDProvider}

import scala.util.Try

@Singleton
class RegistrationFacade @Inject() (
    userDAO:UserDAO,
    user:User,
    timeProvider: TimeProvider,
    uUIDProvider: UUIDProvider)
  extends RegistrationAPI {

  override def signUp(registrationMessage: RegistrationMessage):Try[User] = {
    val hashedPassword = BCrypt.hashpw(registrationMessage.password, BCrypt.gensalt())
    user.create(None,
        registrationMessage.maybeUsername.getOrElse(registrationMessage.email),
        registrationMessage.email,
        hashedPassword,
        isActive = true,
        Some(timeProvider.now()))
      .add(userDAO, uUIDProvider)
  }

  override def isUsernameIsAvailable(username:String): Boolean = userDAO.byUsername(username).isEmpty

  override def isEmailIsAvailable(email:String): Boolean = userDAO.byEmail(email).isEmpty

}
