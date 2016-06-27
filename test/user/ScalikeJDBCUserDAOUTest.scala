package user

import db.{TestDBConnection, TestScalikeJDBCSessionProvider}
import org.flywaydb.core.Flyway
import org.scalatest.TryValues._
import org.scalatest.fixture.FlatSpec
import org.scalatest.{BeforeAndAfterEach, ShouldMatchers}
import scalikejdbc._
import scalikejdbc.scalatest.AutoRollback
import user.UserStatus._

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

  "retrieving a user by user username" should "return the user with that username added the latest" in
  { implicit  session =>
    new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
    .byUsername(" ALIce", authenticationUserFilter) should contain(alice)
  }

  it should "return empty if there is no matching username" in { implicit  session =>
    new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
    .byUsername("zoe", authenticationUserFilter) shouldBe empty
  }

  "retrieving a user by email" should
  "return a the user with that email address added the latest, if that user is active " in { implicit session =>
    new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
      .byEmail("AlIcE@aLIcE.CoM ", authenticationUserFilter) should contain(alice)
  }

  it should "return empty if the latest matching email is inactive" in { implicit  session =>
    new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
    .byUsername("charlie@charlie.com", authenticationUserFilter) shouldBe empty
  }

  "adding a user for the first time (no existing user has this email or username)" should
  "add the user with the properties given in the user object" in { implicit session =>
    val expectedUser =
      TestUserImpl(Some(id6), "newuser", "newuser@newuser.com", "password", Active, Some(now))
    new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
      .addFirstTime(expectedUser, now, id6, registrationUserFilter, authenticationUserFilter).success.value shouldBe
    expectedUser.copy(userStatus = Active)
  }

  "adding a user for the first time (no active existing user has this email, but an inactive one does)" should
  "add the user with the properties given in the user object" in { implicit session =>
    val expectedUser =
      TestUserImpl(Some(id6), "newuser", "charlie@charlie.com", "password", Active, Some(now))
    new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
    .addFirstTime(expectedUser, now, id6, authenticationUserFilter, authenticationUserFilter).success.value shouldBe
    expectedUser.copy(userStatus = Active)
  }

  "adding a user for the first time (no active existing user has this username, but an inactive one does)" should
  "add the user with the properties given in the user object" in { implicit session =>
    val expectedUser =
      TestUserImpl(Some(id6), "charlie", "newuser@newuser.com", "password", Active, Some(now))
    new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
      .addFirstTime(expectedUser, now, id6, registrationUserFilter, authenticationUserFilter).success.value shouldBe
      expectedUser.copy(userStatus = Active)
  }

  "adding a user with an email address that is already active in the db" should "fail" in { implicit session =>
    val duplicateActiveEmailUser =
      TestUserImpl(Some(id6), "newuser", "alice@alice.com", "password", Active, Some(now))
    new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
      .addFirstTime(duplicateActiveEmailUser, now, id6, registrationUserFilter, authenticationUserFilter)
      .failure.exception shouldBe a[RuntimeException]
  }

  "adding a user with a username that is already active in the db" should "fail" in { implicit session =>
    val duplicateActiveUsernameUser =
      TestUserImpl(Some(id6), "boB", "newuser@newuser.com", "password", Active, Some(now))
    new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
      .addFirstTime(duplicateActiveUsernameUser, now, id6, registrationUserFilter, authenticationUserFilter)
      .failure.exception shouldBe a[RuntimeException]
  }

  "adding a user with a username that matches an email address of an active user" should "fail" in { implicit session =>
    val usernameIsExistingEmail =
      TestUserImpl(Some(id6), "bob@bob.com", "newuser@newuser.com", "password", Active, Some(now))
    new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
      .addFirstTime(usernameIsExistingEmail, now, id6, registrationUserFilter, authenticationUserFilter)
      .failure.exception shouldBe a[RuntimeException]
  }

  "retrieving a user by id" should "retrieve the user with the matching parent id that was added the latest" +
    " if that user is active, otherwise it should return none" in { implicit session =>
    val userDAO =
      new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)

    userDAO.by(id4, authenticationUserFilter) shouldBe empty
    userDAO.by(id1, authenticationUserFilter) should contain(alice)
   }

}
