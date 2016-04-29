package user

import com.google.inject.Inject

class UserFacade @Inject() (userDAO:UserDAO) extends UserAPI {

  override def find(id:String):Option[UserMessage] =
    new UserFetcher(userDAO).byUserName(id).map(toUserMessage)

  private def toUserMessage(user:User):UserMessage = UserMessage(user.maybeId, user.maybeUserName, user.email)
}
