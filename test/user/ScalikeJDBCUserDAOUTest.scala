package user

import db.{TestDBConnection, TestScalikeJDBCSessionProvider}
import org.scalatest.TryValues._
import org.scalatest.fixture.FlatSpec
import org.scalatest.{BeforeAndAfterEach, ShouldMatchers}
import registration.PasswordHasher.hash
import scalikejdbc._
import scalikejdbc.scalatest.AutoRollback
import user.UserStatus._
import util.TestTimeProviderImpl

import scala.util.Success

class ScalikeJDBCUserDAOUTest
  extends FlatSpec
  with ShouldMatchers
  with AutoRollback
  with UserFixture
  with BeforeAndAfterEach
  with TestDBConnection {

  override def fixture(implicit session: DBSession) {
    super.fixture
    sqlToAddUsers.foreach(_.update.apply())
  }

  override def beforeEach() {
    dBConfig.setUpAllDB()
    super.beforeEach()
  }

  override def afterEach() {
    dBConfig.closeAll()
    super.afterEach()
  }

  "retrieving a user by user username" should "return the user with that username added the latest" in { implicit session =>
    makeDAO(session).byUsername(" ALIce", authenticationUserFilter) should contain(alice)
  }

  it should "return empty if there is no matching username" in { implicit  session =>
    makeDAO(session).byUsername("zoe", authenticationUserFilter) shouldBe empty
  }

  "retrieving a user by email" should
  "return a the user with that email address added the latest, if that user is active " in { implicit session =>
    makeDAO(session).byEmail("AlIcE@aLIcE.CoM ", authenticationUserFilter) should contain(alice)
  }

  it should "return empty if the latest matching email is inactive" in { implicit  session =>
    makeDAO(session).byUsername("charlie@charlie.com", authenticationUserFilter) shouldBe empty
  }

  "adding a user for the first time (no existing user has this email or username)" should
  "add the user with the properties given in the user object" in { implicit session =>
    val expectedUser =
      TestUserImpl(Some(id6), "newuser", "newuser@newuser.com", "password", Active, Some(now))

    makeDAO(session)
    .add(expectedUser, now, id6, registrationUserFilter, authenticationUserFilter).success.value shouldBe expectedUser
  }

  "adding a user for the first time (no active existing user has this email, but an inactive one does)" should
  "add the user with the properties given in the user object" in { implicit session =>
    val expectedUser =
      TestUserImpl(Some(id6), "newuser", "charlie@charlie.com", "password", Active, Some(now))

    makeDAO(session).add(expectedUser, now, id6, authenticationUserFilter, authenticationUserFilter).success.value shouldBe
      expectedUser
  }

  "adding a user for the first time (no active existing user has this username, but an inactive one does)" should
  "add the user with the properties given in the user object" in { implicit session =>
    val expectedUser =
      TestUserImpl(Some(id6), "charlie", "newuser@newuser.com", "password", Active, Some(now))

    makeDAO(session).add(expectedUser, now, id6, registrationUserFilter, authenticationUserFilter).success.value shouldBe
      expectedUser
  }

  "adding a user with an email address that is already active in the db" should "fail" in { implicit session =>
    val duplicateActiveEmailUser =
      TestUserImpl(Some(id6), "newuser", "alice@alice.com", "password", Active, Some(now))

    makeDAO(session)
    .add(duplicateActiveEmailUser, now, id6, registrationUserFilter, authenticationUserFilter)
    .failure.exception shouldBe a[RuntimeException]
  }

  "adding a user with a username that is already active in the db" should "fail" in { implicit session =>
    val duplicateActiveUsernameUser =
      TestUserImpl(Some(id6), "boB", "newuser@newuser.com", "password", Active, Some(now))

    makeDAO(session)
    .add(duplicateActiveUsernameUser, now, id6, registrationUserFilter, authenticationUserFilter)
    .failure.exception shouldBe a[RuntimeException]
  }

  "adding a user with a username that matches an email address of an active user" should "fail" in { implicit session =>
    val usernameIsExistingEmail =
      TestUserImpl(Some(id6), "bob@bob.com", "newuser@newuser.com", "password", Active, Some(now))

    makeDAO(session)
    .add(usernameIsExistingEmail, now, id6, registrationUserFilter, authenticationUserFilter)
    .failure.exception shouldBe a[RuntimeException]
  }

  "retrieving a user by id" should "retrieve the user with the matching parent id that was added the latest" +
    " if that user is active, otherwise it should return none" in { implicit session =>
    val userDAO = makeDAO(session)

    userDAO.by(id4, authenticationUserFilter) shouldBe empty
    userDAO.by(id1, authenticationUserFilter) should contain(alice)
   }

  "changing the username" should "succeed if the username if available" in { implicit session =>
    val userDAO = makeDAO(session)
    val newUsername = "alice2"

    userDAO.changeUsername(id1, newUsername, TestTimeProviderImpl.now(), authenticationUserFilter)
    .success.value.username shouldBe newUsername
  }

  it should "fail if the username belongs to a non-inactive user" in { implicit session =>
    val userDAO = makeDAO(session)
    val newUsername = " chaRLIE "

    userDAO.changeUsername(id1, newUsername, TestTimeProviderImpl.now(), authenticationUserFilter)
    .failure.exception shouldBe a[RuntimeException]
  }

  it should "fail if the username is the same as a non-inactive user's email" in { implicit session =>
    val userDAO = makeDAO(session)
    val newUsername = " bob@bob.com "

    userDAO.changeUsername(id1, newUsername, TestTimeProviderImpl.now(), authenticationUserFilter)
    .failure.exception shouldBe a[RuntimeException]
  }

  "changing the users password" should "change the user's password" in { implicit session =>
    val userDAO = makeDAO(session)
    val newHashedPassword = hash("some_new_password")

    userDAO.changePassword(id3, newHashedPassword, now.plusMillis(1)) shouldBe a[Success[_]]
    userDAO.by(id3, authenticationUserFilter).map(_.hashedPassword) should contain(newHashedPassword)
  }

  it should "fail if the user id does not represent a user in the db" in { implicit session =>
    val userDAO = makeDAO(session)
    val newHashedPassword = hash("some_new_password")

    userDAO.changePassword(idNonExistentUser, newHashedPassword, now.plusMillis(1))
    .failure.exception shouldBe a[RuntimeException]
  }

  private def makeDAO(session:DBSession) = 
    new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
  
}
