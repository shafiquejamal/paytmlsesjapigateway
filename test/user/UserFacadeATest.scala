package user

import com.eigenroute.scalikejdbctesthelpers.{TestDBConnection, CrauthAutoRollback, TestScalikeJDBCSessionProvider}
import org.scalatest.TryValues._
import org.scalatest._
import org.scalatest.fixture.FlatSpec
import scalikejdbc.DBSession
import util.TestTimeProviderImpl

import scala.util.{Failure, Success}

class UserFacadeATest
  extends FlatSpec
  with ShouldMatchers
  with CrauthAutoRollback
  with UserFixture
  with BeforeAndAfterEach
  with TestDBConnection {

  val converter = new WrappedResultSetToTestUserConverterImpl()
  val user = new TestUserImpl()

  "changing the username" should "change the users username if the username is available" in { implicit session =>
    val newUsername = "alice2"
    val user = makeAPI(session).changeUsername(id1, ChangeUsernameMessage(newUsername)).success.value

    user.maybeId shouldEqual alice.maybeId
    user.username shouldEqual newUsername
  }

  it should "fail if the username is not available" in { implicit session =>
    makeAPI(session).changeUsername(id1, ChangeUsernameMessage("bob@bob.com")).failure.exception shouldBe a[RuntimeException]
  }

  "changing the password" should "change the password if the user exists in the DB" in { implicit session =>
    val newPassword = "bobs_new_password8"
    val changePasswordMessage = ChangePasswordMessage("passwordBobID3", newPassword)

    val maybeUser = makeAPI(session).changePassword(id3, changePasswordMessage)
    maybeUser shouldBe a[Success[_]]
    maybeUser.success.value.maybeId should contain(id3)
  }

  it should "fail if the user does not exist in the DB" in { implicit session =>
    makeAPI(session).changePassword(idNonExistentUser, ChangePasswordMessage("passwordBobID3", "irrelevant")) shouldBe a[Failure[_]]
  }

  it should "fail if the given current password is wrong" in { implicit session =>
    makeAPI(session).changePassword(id3, ChangePasswordMessage("wrong_password", "irrelevant")) shouldBe a[Failure[_]]
  }

  "finding a user by email" should "succeed if the email matches any user (blocked, active, univerified)" in { implicit session =>
    makeAPI(session).findByEmailLatest("diane@diane.com").map(_.username) should contain("diane")
    makeAPI(session).findByEmailLatest("charlie@charlie.com").map(_.username) should contain("charlie")
    makeAPI(session).findByEmailLatest("alice@alice.com").map(_.username) should contain("alice")
    makeAPI(session).findByEmailLatest("bob@bob.com").map(_.username) should contain("bob")
  }

  it should "fail if the email does not match any user in the database" in { implicit session =>
    makeAPI(session).findByEmailLatest("non@existent.com").map(_.username) shouldBe empty
  }

  "finding an unverified user by email" should "return the matching unverified user" in { implicit session =>
    makeAPI(session).findUnverifiedUser("charlie@charlie.com").map(_.username) should contain("charlie")
  }

  it should "fail if the user is non-unverified" in { implicit session =>
    makeAPI(session).findUnverifiedUser("diane@diane.com").map(_.username) shouldBe empty
    makeAPI(session).findUnverifiedUser("alice@alice.com").map(_.username) shouldBe empty
    makeAPI(session).findUnverifiedUser("bob@bob.com").map(_.username) shouldBe empty
  }

  "Querying the userId by username" should "return the userid of the user with the correpsonding " +
  "username" in { implicit session =>
    makeAPI(session).by("charlie") shouldBe empty
    makeAPI(session).by("diane") shouldBe empty
    makeAPI(session).by("aLIce") should contain(id1)
    makeAPI(session).by("boB") should contain(id3)
  }

  "Querying the username by userId" should "return the username of the user with the correpsonding " +
  "userId" in { implicit session =>
    makeAPI(session).by(id4) shouldBe empty
    makeAPI(session).by(id7) shouldBe empty
    makeAPI(session).by(id1) should contain("alice")
    makeAPI(session).by(id3) should contain("bob")
  }

  private def makeAPI(session:DBSession) = {
    val userDAO = new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
    new UserFacade(userDAO, new TestTimeProviderImpl())
  }
}
