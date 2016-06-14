package user

import java.util.UUID

import com.google.inject.Inject
import entity.User
import org.joda.time.DateTime

import scala.util._

class UserCreator @Inject() (userDAO: UserDAO, user:User) {

  def signUp(uUID: UUID = UUID.randomUUID()):Try[User] = {
    val maybeExistingUserByEmail = userDAO.byEmail(user.email)
    val maybeExistingUserByUsername = user.maybeUserName flatMap { userName => userDAO.byUserName(userName) }

    if (maybeExistingUserByEmail.isEmpty && maybeExistingUserByUsername.isEmpty) {
      userDAO.addFirstTime(user, user.maybeCreated.getOrElse(DateTime.now), uUID)
    } else {
      Failure(new RuntimeException("This user is already in the database."))
    }
  }

}
