package chat

import java.util.UUID

import access.JWTParamsProvider
import access.authentication.AuthenticationAPI
import akka.actor.ActorSystem
import akka.stream.{Materializer, OverflowStrategy}
import com.google.inject.Inject
import play.Configuration
import play.api.mvc._
import util.{UUIDProvider, TimeProvider}

class ChatController @Inject() (
    authenticationAPI: AuthenticationAPI,
    jWTParamsProvider: JWTParamsProvider,
    uUIDProvider: UUIDProvider,
    configuration: Configuration,
    timeProvider: TimeProvider,
    system: ActorSystem,
    materializer: Materializer) extends Controller {

  def chat(token: String) = WebSocket.accept[String, String] { request =>

    implicit val mat = materializer
    implicit val actorRefFactory = system

    new ChatAuthenticator(authenticationAPI, jWTParamsProvider, configuration, timeProvider)
    .decodeAndValidateToken(
      token,
      (uUID: UUID) => BetterActorFlow.namedActorRef( client =>
        ChatActor.props(client), 16, OverflowStrategy.dropNew, uUID.toString + "_" + uUIDProvider.randomUUID().toString),
      null
    )

  }

}
