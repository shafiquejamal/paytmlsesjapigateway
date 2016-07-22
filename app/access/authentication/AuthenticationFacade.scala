package access.authentication

import java.util.UUID

import com.google.inject.{Inject, Singleton}
import user.UserStatus._
import user.{User, UserDAO}
import util.Password._
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
      userDAO.addPasswordResetCode(id, passwordResetCode, timeProvider.now(), active = true)
     )
   )

 override def retrievePasswordResetCode(email: String): Option[PasswordResetCodeAndDate] =
   userDAO.byEmail(email, (user:User) => true).fold[Option[PasswordResetCodeAndDate]](None)(user =>
    user.maybeId.fold[Option[PasswordResetCodeAndDate]](None)(id =>
      userDAO.passwordResetCode(id)
    )
  )

 override def resetPassword(email:String, code:String, newPassword:String): Try[User] =
   userDAO.byEmail(email, authenticationUserFilter)
   .fold[Try[User]](Failure(new RuntimeException("User does not exist"))){ retrievedUser =>
     retrievedUser.maybeId.fold[Try[User]](Failure(new RuntimeException("User does not exist"))){userId =>
       userDAO.passwordResetCode(userId, code)
       .fold[Try[User]](Failure(new RuntimeException("Code does not exist for this user"))){ _ =>
         userDAO.changePassword(userId, hash(newPassword), timeProvider.now())
       }
     }
   }

}
