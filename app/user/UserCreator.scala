package user

import com.google.inject.Inject

import scala.util._

class UserCreator @Inject() (userDAO: UserDAO) {

  def signUp(user:User):Try[User] = {
    val maybeExistingUserByEmail = userDAO.byEmail(user.email)
    val maybeExistingUserByUsername = user.maybeUserName flatMap { userName => userDAO.byUserName(userName) }

    if (maybeExistingUserByEmail.isEmpty && maybeExistingUserByUsername.isEmpty) {
      userDAO.addFirstTime(user)
    } else {
      Failure(new RuntimeException("This user is already in the database."))
    }

  }

}
