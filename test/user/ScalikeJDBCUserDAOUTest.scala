package user

import java.util.UUID

import org.flywaydb.core.Flyway

import org.scalatest.ShouldMatchers
import org.scalatest.fixture.FlatSpec
import scalikejdbc._
import scalikejdbc.scalatest.AutoRollback
import org.joda.time.DateTime
import org.scalatest.TryValues._

class ScalikeJDBCUserDAOUTest extends FlatSpec with ShouldMatchers with AutoRollback {

  val now = DateTime.now
  val later = now.plusDays(1)
  val id1 = UUID.randomUUID()
  val id2 = UUID.randomUUID()
  val id3 = UUID.randomUUID()
  val id4 = UUID.randomUUID()
  val id5 = UUID.randomUUID()

  Class.forName("org.h2.Driver")
  ConnectionPool.singleton("jdbc:h2:mem:hello", "user", "pass")

  override def fixture(implicit session: DBSession) {
    val flyway = new Flyway()
    flyway.setDataSource("jdbc:h2:mem:hello", "user", "pass")
    flyway.migrate()
    sql"insert into xuser  (id, username, email, password, isactive, created) values (${id1}, 'alice', 'alice@alice.com', 'password', TRUE, ${now})".update.apply()
    sql"insert into xuser  (id, username, email, password, isactive, created) values (${id2}, 'alice', 'alice@alice.com', 'password', TRUE, ${later})".update.apply()
    sql"insert into xuser  (id, username, email, password, isactive, created) values (${id3}, 'bob', 'bob@bob.com', 'password', TRUE, ${now})".update.apply()
    sql"insert into xuser  (id, username, email, password, isactive, created) values (${id4}, 'charlie', 'charlie@charlie.com', 'password', TRUE, ${now})".update.apply()
    sql"insert into xuser  (id, username, email, password, isactive, created) values (${id5}, 'charlie', 'charlie@charlie.com', 'password', FALSE, ${later})".update.apply()
  }

  "retrieving a user by user username" should "return the user with that username added the latest" in
  { implicit  session =>
    val expectedUser = User(Some(id2), Some("alice"), "alice@alice.com", "password",
                            isActive = true, Some(later), None)
    new ScalikeJDBCUserDAO().UserByUserName("ALIce") should contain(expectedUser)
  }

  it should "return empty if there is no matching username" in { implicit  session =>
    new ScalikeJDBCUserDAO().UserByUserName("zoe")(session) shouldBe empty
  }

  "retrieving a user by email" should "return a the user with that email address added the latest " in { implicit session =>
    val expectedUser = User(Some(id5), Some("charlie"), "charlie@charlie.com", "password",
                            isActive = false, Some(later), None)
    new ScalikeJDBCUserDAO().UserByEmail("ChArLie@cHaRlIe.com") should contain(expectedUser)
  }

  it should "return empty if the latest matching email is inactive" in { implicit  session =>
    new ScalikeJDBCUserDAO().UserByUserName("zoe@zoe.com")(session) shouldBe empty
  }

  "adding a user for the first time (no existing user has this email or username)" should "add the user with the properties" +
    " given in the user object" in { implicit session =>
    val now = DateTime.now
    val id6 = UUID.randomUUID()
    val expectedUser =
      User(Some(id6), Some("newuser"), "newuser@newuser.com", "password", isActive = false, Some(now), Some(id6))
    val maybeAddedUser = new ScalikeJDBCUserDAO().addUserFirstTime(expectedUser, now, id6)
    maybeAddedUser.success.value shouldBe expectedUser
  }


}
