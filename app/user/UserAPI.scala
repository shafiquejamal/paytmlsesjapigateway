package user

import scala.util.Try

trait UserAPI {

  def changeUsername(changeUsernameMessage: ChangeUsernameMessage): Try[User]

  def changePassword(changePasswordMessage: ChangePasswordMessage): Try[User]

}
