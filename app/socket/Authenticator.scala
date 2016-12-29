package socket

import java.util.UUID

import access.authentication._
import access.{AllowedTokens, JWTKeysProvider, MultiUse}
import akka.actor._
import chat.ClientPaths.namedClientActorName
import chat.{ChatContactAPI, ChatMessageAPI}
import play.Configuration
import user.UserAPI
import util.{TimeProvider, UUIDProvider}

class Authenticator (
                      chatAuthenticator: SocketAuthenticator,
                      userAPI: UserAPI,
                      chatMessageAPI: ChatMessageAPI,
                      chatContactsAPI: ChatContactAPI,
                      authenticationAPI: AuthenticationAPI,
                      jWTParamsProvider: JWTKeysProvider,
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
      val maybeValidUser = chatAuthenticator.decodeAndValidateToken[Option[(UUID, String)]](
          authenticationRequest.jwt, (uUID, clientUsername) => Some(uUID, clientUsername), None, AllowedTokens(MultiUse))

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
              namedClient, userAPI, chatMessageAPI, chatContactsAPI, clientId, clientUsername, timeProvider, uUIDProvider))
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
             chatAuthenticator: SocketAuthenticator,
             userAPI: UserAPI,
             chatMessageAPI: ChatMessageAPI,
             chatContactsAPI: ChatContactAPI,
             authenticationAPI: AuthenticationAPI,
             jWTParamsProvider: JWTKeysProvider,
             configuration: Configuration,
             timeProvider: TimeProvider,
             uUIDProvider: UUIDProvider,
             unnamedClient: ActorRef
    ) =
    Props(
      new Authenticator(
        chatAuthenticator,
        userAPI,
        chatMessageAPI,
        chatContactsAPI,
        authenticationAPI,
        jWTParamsProvider,
        configuration,
        timeProvider,
        uUIDProvider,
        unnamedClient))

}
