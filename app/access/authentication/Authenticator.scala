package access.authentication

import java.util.UUID

import access._
import akka.actor._
import com.eigenroute.id.UUIDProvider
import com.eigenroute.time.TimeProvider
import entrypoint.ClientPaths._
import entrypoint._
import play.api.Configuration

class Authenticator (
    userChecker: UserChecker,
    userAPI: UserAPI,
    authenticationAPI: AuthenticationAPI,
    jWTAlgorithmProvider: JWTAlgorithmProvider,
    jWTPublicKeyProvider: JWTPublicKeyProvider,
    configuration: Configuration,
    timeProvider: TimeProvider,
    uUIDProvider: UUIDProvider,
    unnamedClient: ActorRef)
  extends Actor
  with ActorLogging {

  var namedClient: ActorRef = _
  var toServerMessageRouter: ActorRef = _
  var clientUserId: UUID = _

  override def receive = {

    case authenticationRequest: ToServerAuthenticateMessage =>
      val maybeValidUser = userChecker.decodeAndValidateToken[Option[(UUID, String)]](
          authenticationRequest.jwt,
          (uUID, clientUsername) => Some(uUID, clientUsername),
          None,
          AllowedTokens(MultiUse))

      maybeValidUser.fold {
        unnamedClient ! ToClientLoginFailedMessage.toJson
      } { case (clientId, clientUsername) =>
        clientUserId = clientId
        namedClient =
          context.actorOf(
            NamedClient.props(unnamedClient), namedClientActorName(clientId, uUIDProvider.randomUUID()))
        toServerMessageRouter =
          context.actorOf(
            ToServerMessageRouter.props(
              namedClient, userAPI, clientId, clientUsername, timeProvider, uUIDProvider))
        namedClient ! ToClientLoginSuccessfulMessage
        context.become(processAuthenticatedRequests)
      }

  }

  def processAuthenticatedRequests: Receive = {

    case authenticationRequest: ToServerAuthenticateMessage =>
      unnamedClient ! ToClientAlreadyAuthenticatedMessage.toJson

    case ToServerLogoutMessage =>
      unnamedClient ! ToClientLoggingOutMessage.toJson
      context.unbecome()

    case msg: ToServerSocketMessage =>
      msg sendTo toServerMessageRouter

  }

}

object Authenticator {

  def props(
      chatAuthenticator: UserChecker,
      userAPI: UserAPI,
      authenticationAPI: AuthenticationAPI,
      jWTAlgorithmProvider: JWTAlgorithmProvider,
      jWTPublicKeyProvider: JWTPublicKeyProvider,
      configuration: Configuration,
      timeProvider: TimeProvider,
      uUIDProvider: UUIDProvider,
      unnamedClient: ActorRef
    ) =
    Props(
      new Authenticator(
        chatAuthenticator,
        userAPI,
        authenticationAPI,
        jWTAlgorithmProvider,
        jWTPublicKeyProvider,
        configuration,
        timeProvider,
        uUIDProvider,
        unnamedClient))

}
