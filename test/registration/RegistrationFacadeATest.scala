package registration

import db.{TestDBConnection, TestScalikeJDBCSessionProvider}
import org.flywaydb.core.Flyway
import org.mindrot.jbcrypt.BCrypt
import org.scalatest.fixture.FlatSpec
import org.scalatest.{BeforeAndAfterEach, Matchers, ShouldMatchers}
import scalikejdbc.DBSession
import scalikejdbc.scalatest.AutoRollback
import user._
import util.{TestTimeProviderImpl, TestUUIDProviderImpl}

class RegistrationFacadeATest
  extends FlatSpec
  with ShouldMatchers
  with Matchers
  with AutoRollback
  with UserFixture
  with TestDBConnection
  with BeforeAndAfterEach {


  override def fixture(implicit session: DBSession) {
    val flyway = new Flyway()
    flyway.setDataSource("jdbc:h2:mem:play", "sa", "")
    flyway.migrate()
    sqlToAddUsers.foreach(_.update.apply())
  }

  val user = new TestUserImpl()
  val testUUIDProviderImpl = TestUUIDProviderImpl
  testUUIDProviderImpl.index(10)

  override def beforeEach() {
    dBConfig.setUpAllDB()
    super.beforeEach()
  }

  override def afterEach() {
    dBConfig.closeAll()
    super.afterEach()
  }

  "signing up" should "add user that does not already exist" in { implicit session =>

    val userDAO =
      new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig)
    val api = new RegistrationFacade(userDAO, user, TestTimeProviderImpl, testUUIDProviderImpl)
    val password = "some non-hashed password"
    val registrationMessage = RegistrationMessage(Some("some user name"), "test@user.com", password)
    val result = api.signUp(registrationMessage)
    result.isSuccess shouldBe true
    result.toOption.foreach { user =>
      user.username shouldBe "some user name"
      user.email shouldBe "test@user.com"
      BCrypt.checkpw(password, user.hashedPassword) shouldBe true
    }

    val duplicateUsername = RegistrationMessage(Some("some useR name"), "un@ique.com", password)

    val resultDuplicateUsername = api.signUp(duplicateUsername)
    resultDuplicateUsername.isFailure shouldBe true

    val duplicateEmail = RegistrationMessage(Some("unique"), "tEst@user.com", password)

    val resultDuplicateEmail = api.signUp(duplicateEmail)
    resultDuplicateEmail.isFailure shouldBe true
  }

  "checking for username" should "return true only if there is no active user with the given username" in
    { implicit session =>

    val userDAO =
      new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig)
    val api = new RegistrationFacade(userDAO, user, TestTimeProviderImpl, testUUIDProviderImpl)
    api.isUsernameIsAvailable("charlie") shouldBe true
    api.isUsernameIsAvailable("alIcE") shouldBe false
    api.isUsernameIsAvailable("bob") shouldBe false
    api.isUsernameIsAvailable("zoe") shouldBe true

  }

  "checking for email" should "return true only if there is no active user with the given email" in { implicit session =>

    val userDAO =
      new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig)
    val api = new RegistrationFacade(userDAO, user, TestTimeProviderImpl, testUUIDProviderImpl)
    api.isEmailIsAvailable("charlie@charlie.com") shouldBe true
    api.isEmailIsAvailable("alIcE@alice.com") shouldBe false
    api.isEmailIsAvailable("bob@bob.com") shouldBe false
    api.isEmailIsAvailable("zoe@zoe.com") shouldBe true

  }

}
