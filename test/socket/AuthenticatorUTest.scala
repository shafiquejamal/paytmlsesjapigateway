package socket

import java.util.UUID

import access.authentication.{AuthenticationAPI, ToClientLoginFailedMessage, ToClientLoginSuccessfulMessage,
ToServerAuthenticateMessage}
import access.{AllowedTokens, MultiUse, TestJWTParamsProviderImpl}
import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpecLike, ShouldMatchers}
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
  val jWTParamsProvider = new TestJWTParamsProviderImpl()
  class NoArgSocketAuthenticator extends SocketAuthenticator(mockAuthenticationAPI, jWTParamsProvider, null, timeProvider)
  val mockChatAuthenticator = mock[NoArgSocketAuthenticator]
  val authenticator =
    system.actorOf(Authenticator.props(
      mockChatAuthenticator,
      mockUserAPI, null, null, mockAuthenticationAPI, jWTParamsProvider, null, timeProvider, uUIDProvider, testActor))

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
