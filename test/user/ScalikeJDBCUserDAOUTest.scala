package user

import access.authentication.PasswordResetCodeAndDate
import com.eigenroute.scalikejdbctesthelpers.{CrauthAutoRollback, TestDBConnection, TestScalikeJDBCSessionProvider}
import org.scalatest.TryValues._
import org.scalatest.fixture.FlatSpec
import org.scalatest.{BeforeAndAfterEach, ShouldMatchers}
import scalikejdbc._
import user.UserStatus._
import util.Password.hash

import scala.util.Success

class ScalikeJDBCUserDAOUTest
  extends FlatSpec
  with ShouldMatchers
  with CrauthAutoRollback
  with UserFixture
  with BeforeAndAfterEach
  with TestDBConnection {

  val converter = new WrappedResultSetToTestUserConverterImpl()

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
    .add(expectedUser, now, id6, authenticationUserFilter).success.value shouldBe expectedUser
  }

  "adding a user for the first time (no active existing user has this email, but an inactive one does)" should
  "add the user with the properties given in the user object" in { implicit session =>
    val expectedUser =
      TestUserImpl(Some(id6), "newuser", "charlie@charlie.com", "password", Active, Some(now))

    makeDAO(session).add(expectedUser, now, id6, authenticationUserFilter).success.value shouldBe
      expectedUser
  }

  "adding a user for the first time (no active existing user has this username, but an inactive one does)" should
  "add the user with the properties given in the user object" in { implicit session =>
    val expectedUser =
      TestUserImpl(Some(id6), "charlie", "newuser@newuser.com", "password", Active, Some(now))

    makeDAO(session).add(expectedUser, now, id6, authenticationUserFilter).success.value shouldBe
      expectedUser
  }

  "adding a user with an email address that is already active in the db" should "fail" in { implicit session =>
    val duplicateActiveEmailUser =
      TestUserImpl(Some(id6), "newuser", "alice@alice.com", "password", Active, Some(now))

    makeDAO(session)
    .add(duplicateActiveEmailUser, now, id6, authenticationUserFilter)
    .failure.exception shouldBe a[RuntimeException]
  }

  "adding a user with a username that is already active in the db" should "fail" in { implicit session =>
    val duplicateActiveUsernameUser =
      TestUserImpl(Some(id6), "boB", "newuser@newuser.com", "password", Active, Some(now))

    makeDAO(session)
    .add(duplicateActiveUsernameUser, now, id6, authenticationUserFilter)
    .failure.exception shouldBe a[RuntimeException]
  }

  "adding a user with a username that matches an email address of an active user" should "fail" in { implicit session =>
    val usernameIsExistingEmail =
      TestUserImpl(Some(id6), "bob@bob.com", "newuser@newuser.com", "password", Active, Some(now))

    makeDAO(session)
    .add(usernameIsExistingEmail, now, id6, authenticationUserFilter)
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

    userDAO.changeUsername(id1, newUsername, timeProvider.now(), authenticationUserFilter)
    .success.value.username shouldBe newUsername
  }

  it should "fail if the username belongs to a non-inactive user (active, admin, etc)" in { implicit session =>
    val userDAO = makeDAO(session)
    val newUsername = " chaRLIE "

    userDAO.changeUsername(id1, newUsername, timeProvider.now(), changeUsernameFilter)
    .failure.exception shouldBe a[RuntimeException]
  }

  it should "fail if the username is the same as a non-inactive user's email" in { implicit session =>
    val userDAO = makeDAO(session)
    val newUsername = " bob@bob.com "

    userDAO.changeUsername(id1, newUsername, timeProvider.now(), authenticationUserFilter)
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

  "adding a status" should "fail if the user does not exists" in { implicit session =>
    val userDAO = makeDAO(session)
    userDAO.addStatus(idNonExistentUser, Active, now).failure.exception shouldBe a[RuntimeException]
  }

  it should "succeed if the user exists" in { implicit session =>
    val userDAO = makeDAO(session)
    userDAO.addStatus(id4, Active, now.plusDays(2)) shouldBe a[Success[_]]
    userDAO.by(id4, (user:User) => true).map(_.userStatus) should contain(Active)
  }

  "adding a password reset code" should "succeed if the user exists" in { implicit session =>
    val userDAO = makeDAO(session)
    val passwordResetCode = "some password reset code"
    userDAO.addPasswordResetCode(id1, passwordResetCode, now, active = true) shouldBe a[Success[_]]
    userDAO.addPasswordResetCode(idNonExistentUser, passwordResetCode, now, active = true)
    .failure.exception shouldBe a[RuntimeException]
  }

  "retrieving an activation code with the created at date" should "return none if there is none" in { implicit session =>
    val userDAO = makeDAO(session)
    userDAO.passwordResetCode(id4) shouldBe empty
  }

  "retrieving an activation code with the created at date" should "return none if the lastest one is inactive" in
  { implicit session =>
    val userDAO = makeDAO(session)
    userDAO.passwordResetCode(id3) shouldBe empty
  }

  it should "return the latest added one if there are multiple" in { implicit session =>
    val userDAO = makeDAO(session)

    userDAO.passwordResetCode(id1) should contain(PasswordResetCodeAndDate(passwordResetCodeAlice2, yesterday.plusMillis(1)))

    userDAO.addPasswordResetCode(id1, "new code", now, active = false)

    userDAO.passwordResetCode(id1) shouldBe empty
  }

  "retrieving an activation code by userid and email" should "return empty if the activation code does not match the" +
  "code in the database for that user" in { implicit session =>
    val userDAO = makeDAO(session)

    userDAO.passwordResetCode(id1) should contain(PasswordResetCodeAndDate(passwordResetCodeAlice2, yesterday.plusMillis(1)))
  }

  it should "return the code and date if the code matches" in { implicit session =>
    val userDAO = makeDAO(session)

    userDAO.passwordResetCode(id1) should contain(PasswordResetCodeAndDate(passwordResetCodeAlice2, yesterday.plusMillis(1)))
  }

  "getting the latest allLogoutDate" should "return empty if there is no entry for that user, and return the latest " +
  "allLogout date if there are multiple allLogout dates." in { implicit session =>
    val userDAO = makeDAO(session)

    userDAO.allLogoutDate(id3) should contain(yesterday.plusMillis(1))
    userDAO.allLogoutDate(id1) shouldBe empty
  }

  "adding an all logout date" should "add an all logout date for the given user if that user exists" in { implicit session =>
    val userDAO = makeDAO(session)

    userDAO.addAllLogoutDate(idNonExistentUser, now, now.plusDays(1)).failure.exception shouldBe a[RuntimeException]

    userDAO.allLogoutDate(id1) shouldBe empty
    userDAO.addAllLogoutDate(id1, now, now.plusDays(1)).success.value.maybeId should contain(id1)
    userDAO.allLogoutDate(id1) should contain(now)
  }

  "validating a single use token" should "return true if there are no existing single use tokens for this " +
  "user" in { implicit session =>
    val userDAO = makeDAO(session)

    userDAO.validateOneTime(
      id1, now, authenticationUserFilter, now, uUIDProvider.randomUUID()).flatMap(_.maybeId) should contain(id1)
    userDAO.validateOneTime(id1, now, authenticationUserFilter, now, uUIDProvider.randomUUID()) shouldBe empty
  }

  it should "return false if the user is not valid" in { implicit session =>
    val userDAO = makeDAO(session)

    userDAO.validateOneTime(id4, now, authenticationUserFilter, now, uUIDProvider.randomUUID()) shouldBe empty
    userDAO.validateOneTime(idNonExistentUser, now, authenticationUserFilter, now, uUIDProvider.randomUUID())
    .shouldBe(empty)
  }

  it should "return true if the latest existing token iat is earlier than the query iat" in { implicit session =>
    val userDAO = makeDAO(session)

    userDAO.validateOneTime(
      id3, now, authenticationUserFilter, now, uUIDProvider.randomUUID()).flatMap(_.maybeId) should contain(id3)
    userDAO.validateOneTime(id3, now, authenticationUserFilter, now, uUIDProvider.randomUUID()) shouldBe empty
  }

  it should "return false if the latest existing token iat is the same as the query iat" in { implicit session =>
    val userDAO = makeDAO(session)

    userDAO.validateOneTime(id3, yesterday, authenticationUserFilter, now, uUIDProvider.randomUUID()) shouldBe empty
  }

  it should "return false if the latest existing token iat is later than the query iat" in { implicit session =>
    val userDAO = makeDAO(session)

    userDAO.validateOneTime(
      id3, dayBeforeYesterday, authenticationUserFilter, now, uUIDProvider.randomUUID()) shouldBe empty
  }

  private def makeDAO(session:DBSession) =
    new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
  
}
