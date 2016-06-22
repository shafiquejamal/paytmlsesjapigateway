package authentication

import java.util.UUID

import com.google.inject.Inject
import entity.User
import user.{UserMessage, UserDAO}

class AuthenticationFacade @Inject() (userDAO:UserDAO, user:User) extends AuthenticationAPI {

  override def user(parentId:UUID): Option[User] = userDAO.by(parentId)

  override def user(userMessage:UserMessage, hashedPassword:String): Option[User] =
    userDAO.byEmail(userMessage.email, hashedPassword).orElse(
      userMessage.maybeUsername.flatMap{ username => userDAO.byUsername(username, hashedPassword) }
    )

}
