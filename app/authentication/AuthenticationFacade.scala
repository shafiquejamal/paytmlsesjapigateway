package authentication

import java.util.UUID

import com.google.inject.{Inject, Singleton}
import org.mindrot.jbcrypt.BCrypt
import user.UserStatus._
import user.{User, UserDAO}

@Singleton
class AuthenticationFacade @Inject() (userDAO:UserDAO, user:User) extends AuthenticationAPI {

  override def user(id:UUID): Option[User] = userDAO.by(id, authenticationUserFilter)

  override def user(authenticationMessage:AuthenticationMessage): Option[User] = {

    val passwordCheck = (user:User) => BCrypt.checkpw(authenticationMessage.password, user.hashedPassword)

    authenticationMessage.maybeEmail.flatMap { email =>
      userDAO.byEmail(email, authenticationUserFilter).filter(passwordCheck)}
        .orElse(authenticationMessage.maybeUsername.flatMap { username =>
          userDAO.byUsername(username, authenticationUserFilter).filter(passwordCheck)})

  }


}
