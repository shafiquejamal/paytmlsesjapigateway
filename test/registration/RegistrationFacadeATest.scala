package registration

import org.flywaydb.core.Flyway
import org.scalatest.fixture.FlatSpec
import org.scalatest.{Matchers, ShouldMatchers}
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc.{ConnectionPool, DBSession}
import user.{ScalikeJDBCUserDAO, UserMessage, WrappedResultSetToUserConverterImpl}

class RegistrationFacadeATest extends FlatSpec with ShouldMatchers with Matchers with AutoRollback {

  Class.forName("org.h2.Driver")
  ConnectionPool.singleton("jdbc:h2:mem:hello", "user", "pass")

  override def fixture(implicit session: DBSession) {
    val flyway = new Flyway()
    flyway.setDataSource("jdbc:h2:mem:hello", "user", "pass")
    flyway.migrate()
  }

  "signing up" should "add user that does not already exist" in { implicit session =>
    val userDAO = new ScalikeJDBCUserDAO(new WrappedResultSetToUserConverterImpl())
    val api = new RegistrationFacade(userDAO)
    val userMessage = UserMessage(None, Some("some user name"), "test@user.com")
    val hashedPassword = "some hashed password"
    val result = api.signUp(userMessage, hashedPassword)
    result.isSuccess shouldBe true
    result.toOption.foreach { user =>
      user.maybeUserName should contain("some user name")
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


}
