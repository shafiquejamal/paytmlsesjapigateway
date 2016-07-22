package access.authentication

import java.util.UUID

import db.{CrauthAutoRollback, TestDBConnection, TestScalikeJDBCSessionProvider}
import org.scalatest._
import org.scalatest.fixture.FlatSpec
import scalikejdbc.DBSession
import user._
import util.TestTimeProviderImpl
import org.scalatest.TryValues._

import scala.util.Success

class AuthenticationFacadeATest
  extends FlatSpec
  with ShouldMatchers
  with Matchers
  with CrauthAutoRollback
  with BeforeAndAfterEach
  with TestDBConnection
  with UserFixture {

  val timeProvider = TestTimeProviderImpl
  val passwordResetCode = "some password reset code"

  "retrieving a user by ID" should "retrieve the latest added user with the given parent ID if that user is" +
    " active, otherwise return empty" in { implicit session =>
    val api = makeAPI(session)

    api.userById(UUID.fromString("00000000-0000-0000-0000-000000000001")).flatMap(_.maybeId) shouldBe alice.maybeId
    api.userById(UUID.fromString("00000000-0000-0000-0000-000000000004")) shouldBe empty
  }

  "retrieving a user using username or email" should "retrieve the user with the matching username or email if that user" +
    " is active and the password matches, otherwise return empty" in { implicit session =>
    val api = makeAPI(session)

    api.user(AuthenticationMessage(Some("aLIce"), Some("wrong@email.com"), "passwordAliceID2"))
      .flatMap(_.maybeId) shouldBe alice.maybeId
    api.user(AuthenticationMessage(Some("wrongUsername"), Some("alicE@alice.com"), "passwordAliceID2"))
      .flatMap(_.maybeId) shouldBe alice.maybeId
    api.user(AuthenticationMessage(Some("wrongUsername"), Some("wrong@Email.com"), "passwordAliceID2")) shouldBe empty
    api.user(AuthenticationMessage(Some("alice"), Some("alice@alice.com"), "passwordAliceID1")) shouldBe empty
    api.user(AuthenticationMessage(Some("charlie"), Some("charlie@charlie.com"), "passwordCharlieID5")) shouldBe empty
    api.user(AuthenticationMessage(Some("charlie"), Some("charlie@charlie.com"), "passwordCharlieID4")) shouldBe empty
  }

  "storing a password reset code" should "fail if the user does not exist" in { implicit session =>
    val api = makeAPI(session)
    api.storePasswordResetCode("nonexistent@user.com", passwordResetCode).failure.exception shouldBe a[RuntimeException]
  }

  "storing a password reset code" should "succeed otherwise" in { implicit session =>
    val api = makeAPI(session)
    api.storePasswordResetCode("alice@alice.com", passwordResetCode) shouldBe a[Success[_]]
  }

  "retrieving a password reset code" should "succeed if one exists" in { implicit session =>
    val api = makeAPI(session)
    api.retrievePasswordResetCode("alice@alice.com") should
      contain(PasswordResetCodeAndDate(passwordResetCodeAlice2, yesterday.plusMillis(1)))
  }

  private def makeAPI(session:DBSession):AuthenticationAPI = {
    val userDAO =
      new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
    new AuthenticationFacade(userDAO, timeProvider)
  }

}
