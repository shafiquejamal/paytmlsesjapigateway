package authentication

import java.util.UUID

import com.google.inject.Inject
import entity.User
import user.UserDAO

class AuthenticationFacade @Inject() (userDAO:UserDAO, user:User) extends AuthenticationAPI {

  override def user(parentId:UUID):Option[User] = userDAO.byParentID(parentId)

}
