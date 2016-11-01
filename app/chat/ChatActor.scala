package chat

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import play.api.libs.json.Json
import user.UserAPI
import util.TimeProvider
import OutgoingChatMessage._

class ChatActor(client: ActorRef, userAPI: UserAPI, clientId: UUID, clientUsername: String, timeProvider: TimeProvider)
  extends Actor
  with ActorLogging {

  import play.api.libs.json.JsValue

  var chatContacts: Map[String, UUID] = Map()

  override def receive = {
    case msg: JsValue =>
      val messageType = (msg \ "messageType").validate[String].getOrElse("")
      val recipient = (msg \ "recipient").validate[String].getOrElse("")
      val messageText = (msg \ "recipient").validate[String].getOrElse("")

      val maybeRecipientId = chatContacts.get(recipient).orElse(userAPI.by(recipient))
      maybeRecipientId foreach { recipientId =>
        val outgoingMessage = OutgoingChatMessage(clientUsername, recipient, messageText, timeProvider.now().getMillis)
        val actorSelection = context.actorSelection(s"/user/${recipientId.toString}*")
        actorSelection ! outgoingMessage
        client ! Json.toJson(outgoingMessage)
      }
    case outgoingMessage @ OutgoingChatMessage(from, to, text, time) =>
        client ! Json.toJson(outgoingMessage)

  }

}


object ChatActor {

  def props(client: ActorRef, userAPI: UserAPI, clientId: UUID, clientUsername: String, timeProvider: TimeProvider) =
    Props(new ChatActor(client, userAPI, clientId, clientUsername, timeProvider))

}

