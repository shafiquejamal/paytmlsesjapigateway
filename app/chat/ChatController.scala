package chat

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.google.inject.Inject
import play.api.libs.json.JsValue
import play.api.mvc._
import play.api.libs.streams._

class ChatController @Inject() (implicit system: ActorSystem, materializer: Materializer) extends Controller {

  def chat = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(client => ChatActor.props(client))
  }

}
