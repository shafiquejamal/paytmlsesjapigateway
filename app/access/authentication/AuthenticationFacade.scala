package access.authentication

import java.util.UUID

import com.google.inject.{Inject, Singleton}
import user.UserStatus._
import user.{User, UserDAO}
import util.Password.passwordCheck
import util.TimeProvider

import scala.util.{Failure, Try}

@Singleton
class AuthenticationFacade @Inject() (userDAO:UserDAO, timeProvider: TimeProvider) extends AuthenticationAPI {

  override def userById(id:UUID): Option[User] = userDAO.by(id, authenticationUserFilter)

  override def user(authenticationMessage:AuthenticationMessage): Option[User] = {
    authenticationMessage.maybeEmail.filter(_.trim.nonEmpty).flatMap { email =>
      userDAO.byEmail(email, authenticationUserFilter).filter(passwordCheck(authenticationMessage.password))}
        .orElse(authenticationMessage.maybeUsername.filter(_.trim.nonEmpty).flatMap { username =>
          userDAO.byUsername(username, authenticationUserFilter).filter(passwordCheck(authenticationMessage.password))})
  }

 override def storePasswordResetCode(email:String, passwordResetCode:String): Try[User] =
   userDAO.byEmail(email, (user:User) => true).fold[Try[User]](Failure(new RuntimeException("User does not exist")))(user =>
     user.maybeId.fold[Try[User]](Failure(new RuntimeException("User does not exist")))(id =>
      userDAO.addPasswordResetCode(id, passwordResetCode, timeProvider.now())
     )
   )


}
