package registration

import org.flywaydb.core.Flyway
import org.scalatest.{Matchers, ShouldMatchers}
import org.scalatest.fixture.FlatSpec
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc.{ConnectionPool, DBSession}
import user.{UserImpl, UserMessage, ScalikeJDBCUserDAO}


class RegistrationFacadeATest extends FlatSpec with ShouldMatchers with Matchers with AutoRollback {

  Class.forName("org.h2.Driver")
  ConnectionPool.singleton("jdbc:h2:mem:hello", "user", "pass")

  override def fixture(implicit session: DBSession) {
    val flyway = new Flyway()
    flyway.setDataSource("jdbc:h2:mem:hello", "user", "pass")
    flyway.migrate()
  }

  "signing up" should "add user that does not already exist" in { implicit session =>
    val userDAO = new ScalikeJDBCUserDAO
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

    val resultDuplicateUsername = api.signUp(userMessage, hashedPassword)
    resultDuplicateUsername.isFailure shouldBe true

  }



}
