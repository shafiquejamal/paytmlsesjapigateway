package user

import java.util.UUID

import org.flywaydb.core.Flyway
import org.flywaydb.core.internal.command.DbMigrate
import org.flywaydb.play.FlywayPlayComponents
import org.scalatest.ShouldMatchers
import org.scalatest.fixture.FlatSpec
import scalikejdbc._
import scalikejdbc.scalatest.AutoRollback
import org.joda.time.DateTime

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
    sql"insert into xuser  (id, name, username, email, password, isactive, created) values (${id1}, 'alica a', 'alice', 'alice@alice.com', 'password', TRUE, ${now})".update.apply()
    sql"insert into xuser  (id, name, username, email, password, isactive, created) values (${id2}, 'alice a', 'alice', 'alice@alice.com', 'password', TRUE, ${later})".update.apply()
    sql"insert into xuser  (id, name, username, email, password, isactive, created) values (${id3}, 'bob b', 'bob', 'bob@bob.com', 'password', TRUE, ${now})".update.apply()
    sql"insert into xuser  (id, name, username, email, password, isactive, created) values (${id4}, 'charlie c', 'charlie', 'charlie@charlie.com', 'password', TRUE, ${now})".update.apply()
    sql"insert into xuser  (id, name, username, email, password, isactive, created) values (${id5}, 'charlie c', 'charlie', 'charlie@charlie.com', 'password', FALSE, ${later})".update.apply()
  }

  "retrieving a user by user username" should "return the user with that username added the latest if that user is active" in
  { implicit  session =>
    val expectedUser = User(Some(id2), Some("alice"), "alice@alice.com", "password",
                            isActive = true, Some(later))
    new ScalikeJDBCUserDAO().UserByUserName("alice") should contain(expectedUser)
  }

  it should "return empty if the latest matching user entry is inactive" in { implicit  session =>
    new ScalikeJDBCUserDAO().UserByUserName("charlie")(session) shouldBe empty
  }

  "retrieving a user by email" should "return a the user with that email address added the latest if that user is active, " +
                                      "and return nothing otherwise" in { implicit session =>
    val expectedUser = User(Some(id2), Some("alice"), "alice@alice.com", "password",
                            isActive = true, Some(later))
    new ScalikeJDBCUserDAO().UserByEmail("alice@alice.com") should contain(expectedUser)
  }

  it should "return empty if the latest matching email is inactive" in { implicit  session =>
    new ScalikeJDBCUserDAO().UserByUserName("charlie@charlie.com")(session) shouldBe empty
  }

}
