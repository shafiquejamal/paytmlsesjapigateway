package socket

import access.JWTKeysProvider
import access.authentication._
import akka.actor.{Actor, ActorLogging, _}
import chat.{ChatContactAPI, ChatMessageAPI}
import play.Configuration
import play.api.libs.json.JsValue
import user.UserAPI
import util.{TimeProvider, UUIDProvider}

class MessageTranslator(
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

  val authenticator =
    context.actorOf(
      Authenticator.props(
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
             jWTParamsProvider: JWTKeysProvider,
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
        jWTParamsProvider,
        configuration,
        timeProvider,
        uUIDProvider,
        unnamedClient))

}