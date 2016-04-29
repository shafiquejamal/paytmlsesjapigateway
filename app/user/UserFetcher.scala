package user

import com.google.inject.Inject

class UserFetcher @Inject() (userDAO:UserDAO) {

  def byUserName(userName:String):Option[User] = userDAO.byUserName(userName)

  def byEmail(email:String):Option[User] = userDAO.byEmail(email)
  
}
