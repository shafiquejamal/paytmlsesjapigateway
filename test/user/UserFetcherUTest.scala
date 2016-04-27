package user

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, ShouldMatchers}

class UserFetcherUTest extends FlatSpec with ShouldMatchers with MockFactory {

  val mockUserDAO = mock[UserDAO]

  "The fetch by id" should "call the userDao and return empty for a non existent user" in {
    val nonExistentUserID = "Some non-existend user id"
    (mockUserDAO.by _).expects(nonExistentUserID).returning(None)
    new UserFetcher(mockUserDAO).by(nonExistentUserID) shouldBe empty
  }

  it should "return a user object for an id that exists" in {
    val validUserId = "some user id"
    val user = User(Some(validUserId), None, None, "a password", isActive = true)
    (mockUserDAO.by _).expects(validUserId).returning(Some(user))
    new UserFetcher(mockUserDAO).by(validUserId) should contain(user)
  }

  it should "return empty if the user returned by the DAO is not active" in {
    val validUserId = "some user id"
    val user = User(Some(validUserId), None, None, "a password", isActive = false)
    (mockUserDAO.by _).expects(validUserId).returning(Some(user))
    new UserFetcher(mockUserDAO).by(validUserId) shouldBe empty
  }

}
