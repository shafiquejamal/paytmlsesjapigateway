package access.authentication

import java.util.UUID

import access._
import akka.actor._
import clientmessaging.ClientPaths._
import clientmessaging.NamedClient
import com.eigenroute.id.UUIDProvider
import com.eigenroute.time.TimeProvider
import communication.{ToClientSocketMessage, ToServerMessageRouter, ToServerSocketMessage}
import entrypoint._
import pdi.jwt.JwtJson
import play.api.Configuration
import play.api.libs.json.Json
import user.UserMessage

class Authenticator (
    userChecker: UserChecker,
    userAPI: UserAPI,
    authenticationAPI: AuthenticationAPI,
    jWTAlgorithmProvider: JWTAlgorithmProvider,
    jWTPublicKeyProvider: JWTPublicKeyProvider,
    jWTPrivateKeyProvider: JWTPrivateKeyProvider,
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
      } { case (clientId, clientUsername) => createNamedClientAndRouter(clientId, clientUsername) }

    case authenticationMessage: AuthenticationMessage =>
      val maybeUserMessage = authenticationAPI.user(authenticationMessage)
      maybeUserMessage.foreach { case UserMessage(Some(clientId), clientUsername, _, _) =>
        createNamedClientAndRouter(clientId, clientUsername)
      }

      val response: ToClientSocketMessage =
        maybeUserMessage.fold[ToClientSocketMessage](ToClientLoginFailedMessage){
        case UserMessage(Some(clientId), clientUsername, clientEmail, _) =>
          val claim = Json.obj("userId" -> clientId.toString, "iat" -> timeProvider.now())
          val jWT = JwtJson.encode(claim, jWTPrivateKeyProvider.privateKey, jWTAlgorithmProvider.algorithm)
          ToClientLoginSuccessfulMessage(SuccessfulLoginPayload(clientUsername, clientEmail, jWT))
      }
      unnamedClient ! response.toJson


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

  private def createNamedClientAndRouter(clientId: UUID, clientUsername: String): Unit = {
    namedClient =
      context.actorOf(
        NamedClient.props(unnamedClient), namedClientActorName(clientId, uUIDProvider.randomUUID()))
    toServerMessageRouter =
      context.actorOf(
        ToServerMessageRouter.props(
          namedClient, userAPI, clientId, clientUsername, timeProvider, uUIDProvider))
    context.become(processAuthenticatedRequests)
  }

}

object Authenticator {

  def props(
      chatAuthenticator: UserChecker,
      userAPI: UserAPI,
      authenticationAPI: AuthenticationAPI,
      jWTAlgorithmProvider: JWTAlgorithmProvider,
      jWTPublicKeyProvider: JWTPublicKeyProvider,
      jWTPrivateKeyProvider: JWTPrivateKeyProvider,
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
        jWTPrivateKeyProvider,
        configuration,
        timeProvider,
        uUIDProvider,
        unnamedClient))

}
