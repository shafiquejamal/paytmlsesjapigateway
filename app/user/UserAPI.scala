package user

import java.util.UUID

import scala.util.Try

trait UserAPI {

  def changeUsername(userId: UUID, changeUsernameMessage: ChangeUsernameMessage): Try[User]

  def changePassword(userId: UUID, changePasswordMessage: ChangePasswordMessage): Try[User]

  def findByEmailLatest(email: String): Option[User]
}
