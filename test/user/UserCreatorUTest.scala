package user

import java.util.UUID

import org.joda.time.DateTime
import org.scalamock.scalatest.MockFactory
import org.scalatest.TryValues._
import org.scalatest.{FlatSpec, ShouldMatchers}

import scala.util.Success

class UserCreatorUTest extends FlatSpec with ShouldMatchers with MockFactory {

  val mockUserDAO = mock[UserDAO]

  "signing up a user" should "return a success if the a user with that username or email does not already exist" in {

    val id = UUID.randomUUID()
    val now = DateTime.now
    val userToAdd = new TestUserImpl(Some(id), None, "some@email.com", "hashedPassword", isActive = true, Some(now), Some(id))
    (mockUserDAO.byEmail _).expects("some@email.com").returning(None)
    (mockUserDAO.addFirstTime _).expects(userToAdd, now, id).returning(Success(userToAdd))
    new UserCreator(mockUserDAO, userToAdd).signUp(id).success.value  shouldBe userToAdd

  }

}
