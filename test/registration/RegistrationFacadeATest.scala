package registration

import db.{TestDBConnection, TestScalikeJDBCSessionProvider}
import org.flywaydb.core.Flyway
import org.scalatest.fixture.FlatSpec
import org.scalatest.{Matchers, ShouldMatchers}
import scalikejdbc.DBSession
import scalikejdbc.scalatest.AutoRollback
import user._

class RegistrationFacadeATest
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

  "signing up" should "add user that does not already exist" in { implicit session =>

    val userDAO =
      new ScalikeJDBCUserDAO(new WrappedResultSetToUserConverterImpl(user), TestScalikeJDBCSessionProvider(session))
    val api = new RegistrationFacade(userDAO, user)
    val userMessage = UserMessage(None, Some("some user name"), "test@user.com")
    val hashedPassword = "some hashed password"
    val result = api.signUp(userMessage, hashedPassword)
    result.isSuccess shouldBe true
    result.toOption.foreach { user =>
      user.userName shouldBe "some user name"
      user.email shouldBe "test@user.com"
      user.hashedPassword shouldBe hashedPassword
    }

    val duplicateUserName = UserMessage(None, Some("some useR name"), "un@ique.com")

    val resultDuplicateUsername = api.signUp(duplicateUserName, hashedPassword)
    resultDuplicateUsername.isFailure shouldBe true

    val duplicateEmail = UserMessage(None, Some("unique"), "tEst@user.com")

    val resultDuplicateEmail = api.signUp(duplicateEmail, hashedPassword)
    resultDuplicateEmail.isFailure shouldBe true
  }

  "checking for username" should "return true only if there is no active user with the given username" in { implicit session =>

    val userDAO =
      new ScalikeJDBCUserDAO(new WrappedResultSetToUserConverterImpl(user), TestScalikeJDBCSessionProvider(session))
    val api = new RegistrationFacade(userDAO, user)
    api.isUsernameIsAvailable("charlie") shouldBe true
    api.isUsernameIsAvailable("alIcE") shouldBe false
    api.isUsernameIsAvailable("bob") shouldBe false
    api.isUsernameIsAvailable("zoe") shouldBe true

  }

  "checking for email" should "return true only if there is no active user with the given email" in { implicit session =>

    val userDAO =
      new ScalikeJDBCUserDAO(new WrappedResultSetToUserConverterImpl(user), TestScalikeJDBCSessionProvider(session))
    val api = new RegistrationFacade(userDAO, user)
    api.isEmailIsAvailable("charlie@charlie.com") shouldBe true
    api.isEmailIsAvailable("alIcE@alice.com") shouldBe false
    api.isEmailIsAvailable("bob@bob.com") shouldBe false
    api.isEmailIsAvailable("zoe@zoe.com") shouldBe true

  }

}
