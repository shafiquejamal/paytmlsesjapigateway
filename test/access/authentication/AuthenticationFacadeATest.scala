package access.authentication

import java.util.UUID

import db.{CrauthAutoRollback, TestDBConnection, TestScalikeJDBCSessionProvider}
import org.scalatest.TryValues._
import org.scalatest._
import org.scalatest.fixture.FlatSpec
import scalikejdbc.DBSession
import user._
import util.TestTimeProviderImpl

import scala.util.Success

class AuthenticationFacadeATest
  extends FlatSpec
  with ShouldMatchers
  with Matchers
  with CrauthAutoRollback
  with BeforeAndAfterEach
  with TestDBConnection
  with UserFixture {

  val timeProvider = new TestTimeProviderImpl()
  val passwordResetCode = "some password reset code"
  val newPassword = "some new password"

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

  "resetting the password" should "succeed if the id and code match what is in the database and the code is valid, " +
  "and it should invalidate the code" in
  { implicit session =>
    val api = makeAPI(session)

    timeProvider.setNow(timeProvider.now().plusDays(1))

    api.resetPassword("alice@alice.com", passwordResetCodeAlice2, newPassword) shouldBe a[Success[_]]
    api.user(AuthenticationMessage(None, Some("alice@alice.com"), newPassword)).map(_.email) should contain("alice@alice.com")
    api.resetPassword("alice@alice.com", passwordResetCodeAlice2, "another new password")
    .failure.exception shouldBe a[RuntimeException]

    timeProvider.setNow(timeProvider.now().minusDays(1))
  }

  it should "fail if the id and code do not match what is in the database" in { implicit session =>
    val api = makeAPI(session)

    api.resetPassword("alice@alice.com", passwordResetCodeAlice1, newPassword).failure.exception shouldBe
    a[RuntimeException]
    api.user(AuthenticationMessage(None, Some("alice@alice.com"), newPassword)) shouldBe empty
  }

  it should "fail if the email is of a non-existent user" in { implicit session =>
    val api = makeAPI(session)

    api.resetPassword("non@existent.com", passwordResetCodeAlice1, newPassword).failure.exception shouldBe
    a[RuntimeException]
    api.user(AuthenticationMessage(None, Some("non@existent.com"), newPassword)) shouldBe empty
  }

  "retrieving the allLogoutDate" should "yield the latest allLogoutDate" in { implicit session =>
    val api = makeAPI(session)

    api.allLogoutDate(id1) shouldBe empty
    api.allLogoutDate(id3) should contain(yesterday.plusMillis(1))
  }

  "logging out all devices" should "succeed if the user exists" in { implicit session =>
    val api = makeAPI(session)

    api.allLogoutDate(id1) shouldBe empty
    api.logoutAllDevices(id1).success.value.maybeId should contain (id1)
    api.allLogoutDate(id1) should contain(now)
  }

  private def makeAPI(session:DBSession):AuthenticationAPI = {
    val userDAO =
      new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
    new AuthenticationFacade(userDAO, timeProvider)
  }

}
