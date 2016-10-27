package chat

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.google.inject.Inject
import play.api.libs.json.JsValue
import play.api.mvc._
import play.api.libs.streams._

class ChatController @Inject() (implicit system: ActorSystem, materializer: Materializer) extends Controller {

  def chat(token: String) = WebSocket.accept[String, String] { request =>
    println(s"token=$token")
    println("request")
    println(request.headers)
    println(request.queryString.get("encoding").mkString("\n"))
    println(request.id)
    ActorFlow.actorRef(client => ChatActor.props(client))
  }

}
