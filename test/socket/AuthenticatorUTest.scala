package socket

import java.io.File
import java.util.UUID

import access._
import access.authentication.{AuthenticationAPI, ToClientLoginFailedMessage, ToClientLoginSuccessfulMessage, ToServerAuthenticateMessage}
import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.typesafe.config.ConfigFactory
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpecLike, ShouldMatchers}
import play.api.Configuration
import user.UserAPI
import util.{StopSystemAfterAll, TestTimeProviderImpl, TestUUIDProviderImpl}

class AuthenticatorUTest
  extends TestKit(ActorSystem("testsystem"))
  with ShouldMatchers
  with FlatSpecLike
  with StopSystemAfterAll
  with MockFactory {

  val mockUserAPI = mock[UserAPI]
  val mockAuthenticationAPI = mock[AuthenticationAPI]

  val timeProvider = new TestTimeProviderImpl()
  val uUIDProvider = new TestUUIDProviderImpl()
  val jWTAlgorithmProvider = new JWTAlgorithmProviderImpl()
  val configuration = new Configuration(ConfigFactory.parseFile(new File("conf/application.test.conf")).resolve())
  val jWTPublicKeyProvider = new JWTPublicKeyProviderImpl(configuration)
  class NoArgSocketAuthenticator extends
    SocketAuthenticator(mockAuthenticationAPI, jWTAlgorithmProvider, jWTPublicKeyProvider, null, timeProvider)
  val mockChatAuthenticator = mock[NoArgSocketAuthenticator]
  val authenticator =
    system.actorOf(Authenticator.props(
      mockChatAuthenticator,
      mockUserAPI,
      null,
      null,
      mockAuthenticationAPI,
      jWTAlgorithmProvider,
      jWTPublicKeyProvider,
      null,
      timeProvider,
      uUIDProvider,
      testActor))

  val id1 = UUID.fromString("00000000-0000-0000-0000-000000000001")
  val fakeJWT = "somejwt"

  "The authenticator" should "return a login failed message if the authentication attempt fails" in {
    (mockChatAuthenticator.decodeAndValidateToken[Option[(UUID, String)]] _)
      .expects(
        fakeJWT,
        *,
        *,
        AllowedTokens(MultiUse)).returning(None)

    authenticator ! ToServerAuthenticateMessage(fakeJWT)

    expectMsg(ToClientLoginFailedMessage.toJson)
  }

  it should "return a login success message if the authentication attempt succeeds" in {
    val username = "some_user_name"
    (mockChatAuthenticator.decodeAndValidateToken[Option[(UUID, String)]] _)
    .expects(
      fakeJWT,
      *,
      *,
      AllowedTokens(MultiUse)).returning(Some(id1, username))

    authenticator ! ToServerAuthenticateMessage(fakeJWT)

    expectMsg(ToClientLoginSuccessfulMessage.toJson)
  }

}
