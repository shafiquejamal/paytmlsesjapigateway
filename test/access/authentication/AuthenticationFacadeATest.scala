package access.authentication

import java.util.UUID

import db.{CrauthAutoRollback, TestDBConnection, TestScalikeJDBCSessionProvider}
import org.scalatest._
import org.scalatest.fixture.FlatSpec
import user._

class AuthenticationFacadeATest
  extends FlatSpec
  with ShouldMatchers
  with Matchers
  with CrauthAutoRollback
  with BeforeAndAfterEach
  with TestDBConnection
  with UserFixture {

  val user = new TestUserImpl()

  "retrieving a user by ID" should "retrieve the latest added user with the given parent ID if that user is" +
    " active, otherwise return empty" in { implicit session =>
    val userDAO =
      new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
    val api = new AuthenticationFacade(userDAO, user)

    api.userById(UUID.fromString("00000000-0000-0000-0000-000000000001")).flatMap(_.maybeId) shouldBe alice.maybeId
    api.userById(UUID.fromString("00000000-0000-0000-0000-000000000004")) shouldBe empty
  }

  "retrieving a user using username or email" should "retrieve the user with the matching username or email if that user" +
    " is active and the password matches, otherwise return empty" in { implicit session =>
    val userDAO =
      new ScalikeJDBCUserDAO(converter, TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
    val api = new AuthenticationFacade(userDAO, user)

    api.user(AuthenticationMessage(Some("aLIce"), Some("wrong@email.com"), "passwordAliceID2"))
      .flatMap(_.maybeId) shouldBe alice.maybeId
    api.user(AuthenticationMessage(Some("wrongUsername"), Some("alicE@alice.com"), "passwordAliceID2"))
      .flatMap(_.maybeId) shouldBe alice.maybeId
    api.user(AuthenticationMessage(Some("wrongUsername"), Some("wrong@Email.com"), "passwordAliceID2")) shouldBe empty
    api.user(AuthenticationMessage(Some("alice"), Some("alice@alice.com"), "passwordAliceID1")) shouldBe empty
    api.user(AuthenticationMessage(Some("charlie"), Some("charlie@charlie.com"), "passwordCharlieID5")) shouldBe empty
    api.user(AuthenticationMessage(Some("charlie"), Some("charlie@charlie.com"), "passwordCharlieID4")) shouldBe empty
  }

}
