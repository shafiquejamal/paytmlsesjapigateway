package user

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, ShouldMatchers}

class UserFetcherUTest extends FlatSpec with ShouldMatchers with MockFactory {

  val mockUserDAO = mock[UserDAO]

  "The fetch by user name" should "call the userDao and return empty for a non existent or inactive user" in {
    val nonExistentUserName = "Some non-existend user name"
    (mockUserDAO.byUserName _).expects(nonExistentUserName).returning(None)
    new UserFetcher(mockUserDAO).byUserName(nonExistentUserName) shouldBe empty
  }

  it should "return a user object for a username that is active" in {
    val validUserName = "some active user name"
    val user = User(null, Some(validUserName), "an@email.address", "a password", isActive = true)
    (mockUserDAO.byUserName _).expects(validUserName).returning(Some(user))
    new UserFetcher(mockUserDAO).byUserName(validUserName) should contain(user)
  }

  "the fetch by email address" should "call the userDao and return empty for a non existent or inactive user" in {
    val inActiveEmail = "notactive@inactive.com"
    (mockUserDAO.byEmail _).expects(inActiveEmail).returning(None)
    new UserFetcher(mockUserDAO).byEmail(inActiveEmail) shouldBe empty
  }

  it should "return a user object for an email address of an active user" in {
    val validEmail = "an@email.address"
    val user = User(null, Some("username"),validEmail, "a password", isActive = true)
    (mockUserDAO.byEmail _).expects(validEmail).returning(Some(user))
    new UserFetcher(mockUserDAO).byEmail(validEmail) should contain(user)
  }

}
