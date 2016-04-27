package user

import com.google.inject.Inject

class UserFacade @Inject() (userDAO:UserDAO) extends UserAPI {

  override def find(id:String):Option[UserMessage] =
    new UserFetcher(userDAO).by(id).map(toUserMessage)

  private def toUserMessage(user:User):UserMessage = UserMessage(user.id.getOrElse(""), user.userName, user.emailAddress)
}
