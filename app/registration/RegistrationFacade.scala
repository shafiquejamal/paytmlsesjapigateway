package registration

import java.util.UUID

import com.google.inject.Inject
import entity.User
import org.joda.time.DateTime
import user.{UserDAO, UserMessage}

import scala.util.Try

class RegistrationFacade @Inject() (userDAO:UserDAO, user:User) extends RegistrationAPI {

  override def signUp(userMessage:UserMessage, hashedPassword:String):Try[User] =
      user
      .create(None, userMessage.maybeUsername.getOrElse(UUID.randomUUID().toString), userMessage.email, hashedPassword,
              isActive = true, Some(DateTime.now), None)
      .add(userDAO)

}
