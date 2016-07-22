package access.authentication

import java.io.File

import com.typesafe.config.ConfigFactory
import communication.TestLinkSender
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, ShouldMatchers}
import play.api.Configuration
import user.UserStatus.Active
import user.{TestUserImpl, UserFixture}
import util.TestTimeProviderImpl

class PasswordResetCodeSenderImplUTest
  extends FlatSpec
  with ShouldMatchers
  with MockFactory
  with UserFixture {

  val timeProvider = new TestTimeProviderImpl()
  val configuration =
    new Configuration(ConfigFactory.parseFile(new File("conf/application.conf")))
  val authenticationAPI = mock[AuthenticationAPI]
  val linkSender = new TestLinkSender()
  val user = new TestUserImpl(Some(id1), "alice", "alice@alice.com", pAlice2, Active, Some(timeProvider.now()))
  val passwordResetCodeSenderImpl =
    new PasswordResetCodeSenderImpl(authenticationAPI, linkSender, timeProvider, configuration)

  "the code sender" should "not generate a new code if a recent one already exists" in {
    val recentPasswordResetCodeAndDate = PasswordResetCodeAndDate("abc", yesterday)
    (authenticationAPI.retrievePasswordResetCode _).expects(user.email).returning(Some(recentPasswordResetCodeAndDate))

    passwordResetCodeSenderImpl.send(user, "localhost:9000")
  }

  it should "generate a new code if one does not already exist" in {
    (authenticationAPI.retrievePasswordResetCode _).expects(user.email).returning(None)
    (authenticationAPI.storePasswordResetCode _).expects(user.email, *)

    passwordResetCodeSenderImpl.send(user, "localhost:9000")
  }
}
