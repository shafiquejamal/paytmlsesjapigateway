package user

import java.util.UUID

import com.google.inject.{Inject, Singleton}
import user.UserStatus._
import util.Password._
import util.TimeProvider

import scala.util.{Failure, Try}

@Singleton
class UserFacade @Inject() (userDAO:UserDAO, timeProvider: TimeProvider) extends UserAPI {

  def changeUsername(userId: UUID, changeUsernameMessage: ChangeUsernameMessage):Try[User] =
    userDAO.changeUsername(
      userId,
      changeUsernameMessage.newUsername,
      timeProvider.now(),
      authenticationUserFilter)

  def changePassword(userId: UUID, changePasswordMessage: ChangePasswordMessage): Try[User] = {

    userDAO
    .by(userId, authenticationUserFilter)
    .filter(passwordCheck(changePasswordMessage.currentPassword))
    .fold[Try[User]](Failure(new RuntimeException("User does not exist in DB")))(user =>
      userDAO.changePassword(userId, hash(changePasswordMessage.newPassword), timeProvider.now())
    )

  }



}
