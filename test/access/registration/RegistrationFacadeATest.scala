package access.registration

import com.eigenroute.id.TestUUIDProviderImpl
import com.eigenroute.scalikejdbctesthelpers.{CrauthAutoRollback, TestDBConnection, TestScalikeJDBCSessionProvider}
import com.eigenroute.time.TestTimeProviderImpl
import org.scalatest.TryValues._
import org.scalatest.fixture.FlatSpec
import org.scalatest.{BeforeAndAfterEach, Matchers, ShouldMatchers}
import scalikejdbc.DBSession
import user._

import scala.util.Success

class RegistrationFacadeATest
  extends FlatSpec
  with ShouldMatchers
  with Matchers
  with CrauthAutoRollback
  with UserFixture
  with BeforeAndAfterEach
  with TestDBConnection {

  val converter = new WrappedResultSetToTestUserConverterImpl()
  val user = new TestUserImpl()
  val testUUIDProviderImpl = new TestUUIDProviderImpl()
  testUUIDProviderImpl.index = 10

  "signing up" should "add user that does not already exist" in { implicit session =>
    val api = makeAPI(session)
    val password = "some non-hashed password"
    val registrationMessage = RegistrationMessage(Some("some user name"), "test@user.com", password)
    val result = api.signUp(registrationMessage, UserStatus.Active)
    result.isSuccess shouldBe true
    result.toOption.foreach { user =>
      user.username shouldBe "some user name"
      user.email shouldBe "test@user.com"
    }

    val duplicateUsername = RegistrationMessage(Some("some useR name"), "un@ique.com", password)

    val resultDuplicateUsername = api.signUp(duplicateUsername, UserStatus.Active)
    resultDuplicateUsername.isFailure shouldBe true

    val duplicateEmail = RegistrationMessage(Some("unique"), "tEst@user.com", password)

    val resultDuplicateEmail = api.signUp(duplicateEmail, UserStatus.Active)
    resultDuplicateEmail.isFailure shouldBe true
  }

  "checking for username" should "return true only if there is no active user with the given username" in
  { implicit session =>
    val api = makeAPI(session)
    api.isUsernameIsAvailable("charlie") shouldBe false
    api.isUsernameIsAvailable("alIcE") shouldBe false
    api.isUsernameIsAvailable("bob") shouldBe false
    api.isUsernameIsAvailable("zoe") shouldBe true
  }

  "checking for email" should "return true only if there is no active user with the given email" +
  "or the email is not valid" in { implicit session =>
    val api = makeAPI(session)
    api.isEmailIsAvailable("charlie@charlie.com") shouldBe false
    api.isEmailIsAvailable("alIcE@alice.com") shouldBe false
    api.isEmailIsAvailable("bob@bob.com") shouldBe false
    api.isEmailIsAvailable("zoe@zoe.com") shouldBe true
  }

  "activating a user" should "fail if the user id does not represent an exising user" in { implicit session =>
    val api = makeAPI(session)
    api.activate(idNonExistentUser).failure.exception shouldBe a[RuntimeException]
  }

  it should "succeed otherwise" in { implicit session =>
    val api = makeAPI(session)
    api.activate(id4) shouldBe a[Success[_]]
  }

  private def makeAPI(session: DBSession) = {
    val userDAO = new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
    new RegistrationFacade(userDAO, user, new TestTimeProviderImpl(), testUUIDProviderImpl)
  }

}
