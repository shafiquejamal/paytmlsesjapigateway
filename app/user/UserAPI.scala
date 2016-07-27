package user

import java.util.UUID

import scala.util.Try

trait UserAPI {

  def changeUsername(userId: UUID, changeUsernameMessage: ChangeUsernameMessage): Try[UserMessage]

  def changePassword(userId: UUID, changePasswordMessage: ChangePasswordMessage): Try[UserMessage]

  def findByEmailLatest(email: String): Option[UserMessage]

  def findUnverifiedUser(email: String): Option[UserMessage]
}
