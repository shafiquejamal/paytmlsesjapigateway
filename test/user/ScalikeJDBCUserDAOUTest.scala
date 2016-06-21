package user

import java.util.UUID

import db.TestScalikeJDBCSessionProvider
import org.flywaydb.core.Flyway
import org.joda.time.DateTime
import org.scalatest.ShouldMatchers
import org.scalatest.TryValues._
import org.scalatest.fixture.FlatSpec
import scalikejdbc._
import scalikejdbc.scalatest.AutoRollback

class ScalikeJDBCUserDAOUTest extends FlatSpec with ShouldMatchers with AutoRollback {

  val now = DateTime.now
  val later = now.plusDays(1)
  val id1 = UUID.fromString("00000000-0000-0000-0000-000000000001")
  val id2 = UUID.fromString("00000000-0000-0000-0000-000000000002")
  val id3 = UUID.fromString("00000000-0000-0000-0000-000000000003")
  val id4 = UUID.fromString("00000000-0000-0000-0000-000000000004")
  val id5 = UUID.fromString("00000000-0000-0000-0000-000000000005")

  Class.forName("org.h2.Driver")
  ConnectionPool.singleton("jdbc:h2:mem:hello", "user", "pass")

  override def fixture(implicit session: DBSession) {
    val flyway = new Flyway()
    flyway.setDataSource("jdbc:h2:mem:hello", "user", "pass")
    flyway.migrate()
    sql"insert into xuser  (id, username, email, password, isactive, created) values (${id1}, 'alice', 'alice@alice.com', 'password', true, ${now})".update.apply()
    sql"insert into xuser  (id, username, email, password, isactive, created, parentid) values (${id2}, 'alice', 'alice@alice.com', 'password', true, ${later}, ${id1})".update.apply()
    sql"insert into xuser  (id, username, email, password, isactive, created) values (${id3}, 'bob', 'bob@bob.com', 'password', true, ${now})".update.apply()
    sql"insert into xuser  (id, username, email, password, isactive, created) values (${id4}, 'charlie', 'charlie@charlie.com', 'password', true, ${now})".update.apply()
    sql"insert into xuser  (id, username, email, password, isactive, created, parentid) values (${id5}, 'charlie', 'charlie@charlie.com', 'password', false, ${later}, ${id4})".update.apply()
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

  "adding a user with an email address that is already active in the db" should "add fail" +
     " given in the user object" in { implicit session =>
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
    val duplicateActiveEmailUser =
      TestUserImpl(Some(id6), "boB", "newuser@newuser.com", "password", isActive = false, Some(now), Some(id6))
    new ScalikeJDBCUserDAO(new WrappedResultSetToTestUserConverterImpl(), TestScalikeJDBCSessionProvider(session))
      .addFirstTime(duplicateActiveEmailUser, now, id6).failure.exception shouldBe a[RuntimeException]
  }

  "retrieving a user by parent id" should "retrieve the user with the matching parent id that was added the latest" in
  { implicit session =>
    val expectedUser =
      TestUserImpl(Some(id5), "charlie", "charlie@charlie.com", "password", isActive = false, Some(later), Some(id4))
    new ScalikeJDBCUserDAO(new WrappedResultSetToTestUserConverterImpl(), TestScalikeJDBCSessionProvider(session))
      .byParentID(id4) should contain(expectedUser)
   }

}
