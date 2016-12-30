package socket

import access.authentication.AuthenticationAPI
import access.{AuthenticatedActionCreator, JWTAlgorithmProvider, JWTPublicKeyProvider}
import akka.actor.ActorSystem
import akka.stream.Materializer
import chat.{ChatContactAPI, ChatMessageAPI}
import com.google.inject.Inject
import play.api.Configuration
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import user.UserAPI
import util.{TimeProvider, UUIDProvider}

class SocketController @Inject()(
    socketAuthenticator: SocketAuthenticator,
    userAPI: UserAPI,
    chatMessageAPI: ChatMessageAPI,
    chatContactsAPI: ChatContactAPI,
    override val authenticationAPI: AuthenticationAPI,
    override val jWTAlgorithmProvider: JWTAlgorithmProvider,
    override val jWTPublicKeyProvider: JWTPublicKeyProvider,
    uUIDProvider: UUIDProvider,
    override val configuration: Configuration,
    override val timeProvider: TimeProvider,
    system: ActorSystem,
    materializer: Materializer)
  extends Controller
  with AuthenticatedActionCreator {

  implicit val mat = materializer
  implicit val actorRefFactory = system

  def connect = {

    WebSocket.accept[JsValue, JsValue] { request =>
      ActorFlow.actorRef(unnamedClient =>
        MessageTranslator.props(
          socketAuthenticator,
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
  }
}
