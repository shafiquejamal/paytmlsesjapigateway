package user

import com.google.inject.{Inject, Singleton}
import user.UserStatus._
import util.Password._
import util.TimeProvider

import scala.util.{Failure, Try}

@Singleton
class UserFacade @Inject() (userDAO:UserDAO, timeProvider: TimeProvider) extends UserAPI {

  def changeUsername(changeUsernameMessage: ChangeUsernameMessage):Try[User] =
    userDAO.changeUsername(
      changeUsernameMessage.userId,
      changeUsernameMessage.newUsername,
      timeProvider.now(),
      authenticationUserFilter)

  def changePassword(changePasswordMessage: ChangePasswordMessage): Try[User] = {

    userDAO
    .by(changePasswordMessage.userId, authenticationUserFilter)
    .filter(passwordCheck(changePasswordMessage.currentPassword))
    .fold[Try[User]](Failure(new RuntimeException("User does not exist in DB")))(user =>
      userDAO.changePassword(changePasswordMessage.userId, hash(changePasswordMessage.newPassword), timeProvider.now())
    )

  }



}
