package user

import java.util.UUID

import db.{TestDBConnection, TestScalikeJDBCSessionProvider}
import org.flywaydb.core.Flyway
import org.joda.time.DateTime
import org.scalatest.ShouldMatchers
import org.scalatest.TryValues._
import org.scalatest.fixture.FlatSpec
import scalikejdbc._
import scalikejdbc.scalatest.AutoRollback

class ScalikeJDBCUserDAOUTest extends FlatSpec with ShouldMatchers with AutoRollback with UserFixture with TestDBConnection {

  override def fixture(implicit session: DBSession) {
    val flyway = new Flyway()
    flyway.setDataSource("jdbc:h2:mem:hello", "user", "pass")
    flyway.migrate()
    sqlToAddUsers.foreach(_.update.apply())
  }

  "retrieving a user by user username" should "return the user with that username added the latest" in
  { implicit  session =>
    val expectedUser = TestUserImpl(Some(id2), "alice", "alice@alice.com", "password",
                            isActive = true, Some(later), Some(id1))
    new ScalikeJDBCUserDAO(new WrappedResultSetToTestUserConverterImpl(), TestScalikeJDBCSessionProvider(session))
      .byUserName("ALIce") should contain(expectedUser)
  }

  it should "return empty if there is no matching username" in { implicit  session =>
    new ScalikeJDBCUserDAO(new WrappedResultSetToTestUserConverterImpl(), TestScalikeJDBCSessionProvider(session))
      .byUserName("zoe") shouldBe empty
  }

  "retrieving a user by email" should
  "return a the user with that email address added the latest, if that user is active " in { implicit session =>
    val expectedUser = TestUserImpl(Some(id5), "alice", "alice@alice.com", "password",
                            isActive = true, Some(later), Some(id4))
    new ScalikeJDBCUserDAO(new WrappedResultSetToTestUserConverterImpl(), TestScalikeJDBCSessionProvider(session))
      .byEmail("AlIcE@aLIcE.CoM") should contain(expectedUser.copy(maybeId = Some(id2), maybeParentId = Some(id1)))
  }

  it should "return empty if the latest matching email is inactive" in { implicit  session =>
    new ScalikeJDBCUserDAO(new WrappedResultSetToTestUserConverterImpl(), TestScalikeJDBCSessionProvider(session))
      .byUserName("charlie@charlie.com") shouldBe empty
  }

  "adding a user for the first time (no existing user has this email or username)" should
  "add the user with the properties given in the user object" in { implicit session =>
    val now = DateTime.now
    val id6 = UUID.randomUUID()
    val expectedUser =
      TestUserImpl(Some(id6), "newuser", "newuser@newuser.com", "password", isActive = false, Some(now), Some(id6))
    new ScalikeJDBCUserDAO(new WrappedResultSetToTestUserConverterImpl(), TestScalikeJDBCSessionProvider(session))
      .addFirstTime(expectedUser, now, id6).success.value shouldBe expectedUser.copy(isActive = true)
  }

  "adding a user for the first time (no active existing user has this email, but an inactive one does)" should
  "add the user with the properties given in the user object" in { implicit session =>
    val now = DateTime.now
    val id6 = UUID.randomUUID()
    val expectedUser =
      TestUserImpl(Some(id6), "newuser", "charlie@charlie.com", "password", isActive = false, Some(now), Some(id6))
    new ScalikeJDBCUserDAO(new WrappedResultSetToTestUserConverterImpl(), TestScalikeJDBCSessionProvider(session))
    .addFirstTime(expectedUser, now, id6).success.value shouldBe expectedUser.copy(isActive = true)
  }

  "adding a user for the first time (no active existing user has this username, but an inactive one does)" should
  "add the user with the properties given in the user object" in { implicit session =>
    val now = DateTime.now
    val id6 = UUID.randomUUID()
    val expectedUser =
      TestUserImpl(Some(id6), "charlie", "newuser@newuser.com", "password", isActive = false, Some(now), Some(id6))
    new ScalikeJDBCUserDAO(new WrappedResultSetToTestUserConverterImpl(), TestScalikeJDBCSessionProvider(session))
      .addFirstTime(expectedUser, now, id6).success.value shouldBe expectedUser.copy(isActive = true)
  }

  "adding a user with an email address that is already active in the db" should "fail" in { implicit session =>
    val now = DateTime.now
    val id6 = UUID.randomUUID()
    val duplicateActiveEmailUser =
      TestUserImpl(Some(id6), "newuser", "alice@alice.com", "password", isActive = false, Some(now), Some(id6))
    new ScalikeJDBCUserDAO(new WrappedResultSetToTestUserConverterImpl(), TestScalikeJDBCSessionProvider(session))
    .addFirstTime(duplicateActiveEmailUser, now, id6).failure.exception shouldBe a[RuntimeException]
  }

  "adding a user with a username that is already active in the db" should "add fail" +
    " given in the user object" in { implicit session =>
    val now = DateTime.now
    val id6 = UUID.randomUUID()
    val duplicateActiveUsernameUser =
      TestUserImpl(Some(id6), "boB", "newuser@newuser.com", "password", isActive = false, Some(now), Some(id6))
    new ScalikeJDBCUserDAO(new WrappedResultSetToTestUserConverterImpl(), TestScalikeJDBCSessionProvider(session))
      .addFirstTime(duplicateActiveUsernameUser, now, id6).failure.exception shouldBe a[RuntimeException]
  }

  "retrieving a user by parent id" should "retrieve the user with the matching parent id that was added the latest" +
    " if that user is active, otherwise it should return none" in { implicit session =>
    val userDAO =
      new ScalikeJDBCUserDAO(new WrappedResultSetToTestUserConverterImpl(), TestScalikeJDBCSessionProvider(session))
    userDAO.byParentID(id4) shouldBe empty

    val expectedUser =
      TestUserImpl(Some(id2), "alice", "alice@alice.com", "password", isActive = true, Some(later), Some(id1))
    userDAO.byParentID(id1) should contain(expectedUser)
   }

}
