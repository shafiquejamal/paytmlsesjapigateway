package user

import scala.util.Try

trait UserAPI {

  def changeUsername(changeUsernameMessage: ChangeUsernameMessage):Try[User]

}
