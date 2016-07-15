package user

import db.{TestDBConnection, TestScalikeJDBCSessionProvider}
import org.mindrot.jbcrypt.BCrypt
import org.scalatest.TryValues._
import org.scalatest._
import org.scalatest.fixture.FlatSpec
import scalikejdbc.DBSession
import scalikejdbc.scalatest.AutoRollback
import util.Password.hash
import util.TestTimeProviderImpl

import scala.util.{Failure, Success}

class UserFacadeATest
  extends FlatSpec
  with ShouldMatchers
  with Matchers
  with AutoRollback
  with UserFixture
  with TestDBConnection
  with BeforeAndAfterEach {

  override def fixture(implicit session: DBSession): Unit = {
    super.fixture
    sqlToAddUsers.foreach(_.update.apply())
  }

  val user = new TestUserImpl()

  override def beforeEach() {
    dBConfig.setUpAllDB()
    super.beforeEach()
  }

  override def afterEach() {
    dBConfig.closeAll()
    super.afterEach()
  }

  "changing the username" should "change the users username if the username is available" in { implicit session =>
    val userDAO = makeDAO(session)
    val api = new UserFacade(userDAO, TestTimeProviderImpl)
    val newUsername = "alice2"
    val user = api.changeUsername(id1, ChangeUsernameMessage(newUsername)).success.value

    user.maybeId shouldEqual alice.maybeId
    user.username shouldEqual newUsername
  }

  it should "fail if the username is not available" in { implicit session =>
    val userDAO = makeDAO(session)
    val api = new UserFacade(userDAO, TestTimeProviderImpl)
    val newUsername = "bob@bob.com"

    api.changeUsername(id1, ChangeUsernameMessage(newUsername)).failure.exception shouldBe a[RuntimeException]
  }

  "changing the password" should "change the password if the user exists in the DB" in { implicit session =>
    val userDAO = makeDAO(session)
    val api = new UserFacade(userDAO, TestTimeProviderImpl)
    val newPassword = "bobs_new_password8"
    val changePasswordMessage = ChangePasswordMessage("passwordBobID3", newPassword)

    val maybeUser = api.changePassword(id3, changePasswordMessage)
    maybeUser shouldBe a[Success[_]]
    maybeUser.success.value.maybeId should contain(id3)
    BCrypt.checkpw(hash(newPassword), maybeUser.success.value.hashedPassword)
  }

  it should "fail if the user does not exist in the DB" in { implicit session =>
    val userDAO = makeDAO(session)
    val api = new UserFacade(userDAO, TestTimeProviderImpl)

    api.changePassword(idNonExistentUser, ChangePasswordMessage("passwordBobID3", "irrelevant")) shouldBe a[Failure[_]]
  }

  it should "fail if the given current password is wrong" in { implicit session =>
    val userDAO = makeDAO(session)
    val api = new UserFacade(userDAO, TestTimeProviderImpl)

    api.changePassword(id3, ChangePasswordMessage("wrong_password", "irrelevant")) shouldBe a[Failure[_]]
  }

  private def makeDAO(session:DBSession) =
    new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
}
