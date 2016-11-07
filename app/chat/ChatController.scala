package chat

import java.util.UUID

import access.authentication.AuthenticationAPI
import access.{AllowedTokens, AuthenticatedActionCreator, JWTParamsProvider, SingleUse}
import akka.actor.ActorSystem
import akka.stream.{Materializer, OverflowStrategy}
import com.google.inject.Inject
import pdi.jwt.JwtJson
import play.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import socket.SocketActor
import user.UserAPI
import util.{TimeProvider, UUIDProvider}

import scala.concurrent.Future

class ChatController @Inject() (
    userAPI: UserAPI,
    chatMessageAPI: ChatMessageAPI,
    chatContactsAPI: ChatContactAPI,
    override val authenticationAPI: AuthenticationAPI,
    override val jWTParamsProvider: JWTParamsProvider,
    uUIDProvider: UUIDProvider,
    override val configuration: Configuration,
    override val timeProvider: TimeProvider,
    system: ActorSystem,
    materializer: Materializer)
  extends Controller
  with AuthenticatedActionCreator {

  def singleUseToken = AuthenticatedAction(parse.json) { request =>
    val claim =
      Json.obj(
        "userId" -> request.userId.toString,
        "username" -> request.username,
        "iat" -> timeProvider.now(),
        "tokenUse" -> "single"
      )
    val jWT = JwtJson.encode(claim, jWTParamsProvider.secretKey, jWTParamsProvider.algorithm)
    Ok(Json.obj("singleUseToken" -> jWT))
  }

  def chat(token: String) = WebSocket.acceptOrResult[JsValue, JsValue] { request =>

    implicit val mat = materializer
    implicit val actorRefFactory = system

    Future.successful(new ChatAuthenticator(authenticationAPI, jWTParamsProvider, configuration, timeProvider)
    .decodeAndValidateToken(
      token,
      (uUID: UUID, username: String) => Right(BetterActorFlow.namedActorRef(
        client => SocketActor.props(client, userAPI, chatMessageAPI, chatContactsAPI, uUID, username, timeProvider, uUIDProvider),
        16,
        OverflowStrategy.dropNew,
        uUID.toString + "_" + uUIDProvider.randomUUID().toString)),
      Left(Forbidden),
      AllowedTokens(SingleUse)
    ))

  }

}
