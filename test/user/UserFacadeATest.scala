package user

import db.{TestDBConnection, TestScalikeJDBCSessionProvider}
import org.scalatest.TryValues._
import org.scalatest._
import org.scalatest.fixture.FlatSpec
import scalikejdbc.DBSession
import scalikejdbc.scalatest.AutoRollback
import util.TestTimeProviderImpl

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

    val userDAO =
      new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
    val api = new UserFacade(userDAO, TestTimeProviderImpl)
    val newUsername = "alice2"
    val user = api.changeUsername(ChangeUsernameMessage(id1, newUsername)).success.value
    user.maybeId shouldEqual alice.maybeId
    user.username shouldEqual newUsername

  }

  it should "fail if the username is not available" in { implicit session =>

    val userDAO =
      new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
    val api = new UserFacade(userDAO, TestTimeProviderImpl)
    val newUsername = "bob@bob.com"
    api.changeUsername(ChangeUsernameMessage(id1, newUsername)).failure.exception shouldBe a[RuntimeException]

  }

}
