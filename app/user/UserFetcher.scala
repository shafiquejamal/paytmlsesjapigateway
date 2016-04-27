package user

import com.google.inject.Inject
import fetcher.Fetcher

class UserFetcher @Inject() (userDAO:UserDAO) extends Fetcher[User] {

  def by(id:String):Option[User] = keepIfActive { userDAO.by(id) }
  
}
