package socket

import access.authentication._
import access.{JWTAlgorithmProvider, JWTPublicKeyProvider}
import akka.actor.{Actor, ActorLogging, _}
import chat.{ChatContactAPI, ChatMessageAPI}
import play.api.Configuration
import play.api.libs.json.JsValue
import user.UserAPI
import util.{TimeProvider, UUIDProvider}

class MessageTranslator(
    chatAuthenticator: SocketAuthenticator,
    userAPI: UserAPI,
    chatMessageAPI: ChatMessageAPI,
    chatContactsAPI: ChatContactAPI,
    authenticationAPI: AuthenticationAPI,
    jWTAlgorithmProvider: JWTAlgorithmProvider,
    jWTPublicKeyProvider: JWTPublicKeyProvider,
    configuration: Configuration,
    timeProvider: TimeProvider,
    uUIDProvider: UUIDProvider,
    unnamedClient: ActorRef)
  extends Actor
  with ActorLogging {

  val authenticator =
    context.actorOf(
      Authenticator.props(
        chatAuthenticator,
        userAPI,
        chatMessageAPI,
        chatContactsAPI,
        authenticationAPI,
        jWTAlgorithmProvider,
        jWTPublicKeyProvider,
        configuration,
        timeProvider,
        uUIDProvider,
        unnamedClient))

  override def receive = {

    case msg: JsValue =>
      val messageType = (msg \ "messageType").validate[String].getOrElse("")
      val socketMessage = ToServerSocketMessageType.from(messageType).socketMessage(msg)
      socketMessage sendTo authenticator

  }

}

object MessageTranslator {

  def props(
    chatAuthenticator: SocketAuthenticator,
    userAPI: UserAPI,
    chatMessageAPI: ChatMessageAPI,
    chatContactsAPI: ChatContactAPI,
    authenticationAPI: AuthenticationAPI,
    jWTAlgorithmProvider: JWTAlgorithmProvider,
    jWTPublicKeyProvider: JWTPublicKeyProvider,
    configuration: Configuration,
    timeProvider: TimeProvider,
    uUIDProvider: UUIDProvider,
    unnamedClient: ActorRef
  ) =
    Props(
      new MessageTranslator(
        chatAuthenticator,
        userAPI,
        chatMessageAPI,
        chatContactsAPI,
        authenticationAPI,
        jWTAlgorithmProvider,
        jWTPublicKeyProvider,
        configuration,
        timeProvider,
        uUIDProvider,
        unnamedClient))

}