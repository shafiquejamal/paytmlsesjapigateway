package user

import java.util.UUID

import com.eigenroute.time.TimeProvider
import com.google.inject.{Inject, Singleton}
import entrypoint.UserAPI
import user.UserMessage._
import user.UserStatus._
import util.Password._

import scala.util.{Failure, Try}

@Singleton
class UserFacade @Inject() (userDAO:UserDAO, timeProvider: TimeProvider) extends UserAPI {

  override def changeUsername(userId: UUID, changeUsernameMessage: ChangeUsernameMessage):Try[UserMessage] =
    userDAO.changeUsername(
      userId,
      changeUsernameMessage.newUsername,
      timeProvider.now(),
      changeUsernameFilter)

  override def changePassword(userId: UUID, changePasswordMessage: ChangePasswordMessage): Try[UserMessage] =
    userDAO
    .by(userId, authenticationUserFilter)
    .filter(passwordCheck(changePasswordMessage.currentPassword))
    .fold[Try[User]](Failure(new RuntimeException("User does not exist in DB")))(user =>
      userDAO.changePassword(userId, hash(changePasswordMessage.newPassword), timeProvider.now())
    )

  override def findByEmailLatest(email:String): Option[UserMessage] = userDAO.byEmail(email, (user:User) => true)

  override def findUnverifiedUser(email:String): Option[UserMessage] =
    userDAO.byEmail(email, (user:User) => user.userStatus == Unverified)

  override def by(username: String): Option[UUID] = userDAO.byUsername(username, authenticationUserFilter).flatMap(_.maybeId)

  override def by(userId: UUID): Option[String] = userDAO.by(userId, authenticationUserFilter).map(_.username)

}
