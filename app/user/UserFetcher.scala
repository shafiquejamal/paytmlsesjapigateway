package user

import com.google.inject.Inject

class UserFetcher @Inject() (userDAO:UserDAO) {

  def byUserName(userName:String):Option[User] = userDAO.byUserName(userName).filter(_.isActive)

  def byEmail(email:String):Option[User] = userDAO.byEmail(email).filter(_.isActive)
  
}
