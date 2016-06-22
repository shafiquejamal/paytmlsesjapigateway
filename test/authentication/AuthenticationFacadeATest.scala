package authentication

import java.util.UUID

import db.{TestScalikeJDBCSessionProvider, TestDBConnection}
import org.flywaydb.core.Flyway
import org.scalatest._
import org.scalatest.fixture.FlatSpec
import scalikejdbc.DBSession
import scalikejdbc.scalatest.AutoRollback
import user._

class AuthenticationFacadeATest
  extends FlatSpec
    with ShouldMatchers
    with Matchers
    with AutoRollback
    with UserFixture
    with TestDBConnection {

  override def fixture(implicit session: DBSession) {
    val flyway = new Flyway()
    flyway.setDataSource("jdbc:h2:mem:hello", "user", "pass")
    flyway.migrate()
    sqlToAddUsers.foreach(_.update.apply())
  }

  val user = new TestUserImpl()

  "retrieving a user using the parent ID" should "retrieve the latest added user with the given parent ID if that user is" +
    "active, otherwise return empty" in { implicit session =>
    val userDAO =
      new ScalikeJDBCUserDAO(new WrappedResultSetToUserConverterImpl(user), TestScalikeJDBCSessionProvider(session))
    val api = new AuthenticationFacade(userDAO, user)

    api.user(UUID.fromString("00000000-0000-0000-0000-000000000001")) should contain(alice2)
    api.user(UUID.fromString("00000000-0000-0000-0000-000000000004")) shouldBe empty
  }

  "retrieving a user using username or email" should "retrieve the latest added user with the given parent ID if that user" +
    " is active and the password matches, otherwise return empty" in { implicit session =>
    val userDAO =
      new ScalikeJDBCUserDAO(new WrappedResultSetToUserConverterImpl(user), TestScalikeJDBCSessionProvider(session))
    val api = new AuthenticationFacade(userDAO, user)

    api.user(UserMessage(None, Some("aLIce"), "wrong-email"), "passwordAliceID2") should contain(alice2)
    api.user(UserMessage(None, Some("wrongUsername"), "alicE@alice.com"), "passwordAliceID2") should contain(alice2)
    api.user(UserMessage(None, Some("wrongUsername"), "wrongEmail"), "passwordAliceID2") shouldBe empty
    api.user(UserMessage(None, Some("alice"), "alice@alice.com"), "passwordAliceID1") shouldBe empty
    api.user(UserMessage(None, Some("charlie"), "charlie@charlie.com"), "passwordCharlieID5") shouldBe empty
    api.user(UserMessage(None, Some("charlie"), "charlie@charlie.com"), "passwordCharlieID4") shouldBe empty
  }

}
