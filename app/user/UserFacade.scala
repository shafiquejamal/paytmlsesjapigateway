package user

import com.google.inject.Inject
import user.UserStatus._
import util.TimeProvider

import scala.util.Try

class UserFacade @Inject() (userDAO:UserDAO, timeProvider: TimeProvider) extends UserAPI {

  def changeUsername(changeUsernameMessage: ChangeUsernameMessage):Try[User] =
    userDAO.changeUsername(
      changeUsernameMessage.userId,
      changeUsernameMessage.newUsername,
      timeProvider.now(),
      authenticationUserFilter)


}
