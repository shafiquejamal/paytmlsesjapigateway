package registration

import com.google.inject.Inject
import entity.User
import org.joda.time.DateTime
import user.{UserDAO, UserImpl, UserMessage}

import scala.util.Try

class RegistrationFacade @Inject() (userDAO:UserDAO) extends RegistrationAPI {

  override def signUp(userMessage:UserMessage, hashedPassword:String):Try[User] =
      new UserImpl(None, userMessage.maybeUsername, userMessage.email, hashedPassword, true, Some(DateTime.now), None).add(userDAO)

}
