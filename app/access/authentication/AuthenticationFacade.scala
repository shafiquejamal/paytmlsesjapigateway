package access.authentication

import java.util.UUID

import com.google.inject.{Inject, Singleton}
import user.UserStatus._
import user.{User, UserDAO}
import util.Password.passwordCheck

@Singleton
class AuthenticationFacade @Inject() (userDAO:UserDAO, user:User) extends AuthenticationAPI {

  override def userById(id:UUID): Option[User] = userDAO.by(id, authenticationUserFilter)

  override def user(authenticationMessage:AuthenticationMessage): Option[User] = {

    authenticationMessage.maybeEmail.flatMap { email =>
      userDAO.byEmail(email, authenticationUserFilter).filter(passwordCheck(authenticationMessage.password))}
        .orElse(authenticationMessage.maybeUsername.flatMap { username =>
          userDAO.byUsername(username, authenticationUserFilter).filter(passwordCheck(authenticationMessage.password))})

  }


}
