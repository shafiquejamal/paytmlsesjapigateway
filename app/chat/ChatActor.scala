package chat

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import play.api.libs.json.Json
import user.UserAPI
import util.{UUIDProvider, TimeProvider}
import OutgoingChatMessage._

class ChatActor(
    client: ActorRef,
    userAPI: UserAPI,
    chatMessageAPI: ChatMessageAPI,
    clientId: UUID,
    clientUsername: String,
    timeProvider: TimeProvider,
    uUIDProvider: UUIDProvider)
  extends Actor
  with ActorLogging {

  import play.api.libs.json.JsValue

  var chatContacts: Map[String, UUID] = Map()

  override def receive = {
    case msg: JsValue =>
      val messageType = (msg \ "messageType").validate[String].getOrElse("")
      val recipient = (msg \ "recipient").validate[String].getOrElse("")
      val messageText = (msg \ "text").validate[String].getOrElse("")

      val maybeRecipientIdFromCache = chatContacts.get(recipient)
      val maybeRecipientId = maybeRecipientIdFromCache.orElse(userAPI.by(recipient))
      maybeRecipientId foreach { recipientId =>
        if (maybeRecipientIdFromCache.isEmpty) chatContacts = chatContacts + (recipient -> recipientId)
        val outgoingMessage = OutgoingChatMessage(clientUsername, recipient, messageText, timeProvider.now().getMillis)
        chatMessageAPI
          .store(OutgoingChatMessageWithVisibility(outgoingMessage, Both, clientId, recipientId, uUIDProvider.randomUUID()))
        val actorSelectionRecipients = context.actorSelection(s"/user/${recipientId.toString}*")
        actorSelectionRecipients ! outgoingMessage
        val actorSelectionSenders = context.actorSelection(s"/user/${clientId.toString}*")
        actorSelectionSenders ! outgoingMessage
      }
    case outgoingMessage @ OutgoingChatMessage(from, to, text, time) =>
        client ! Json.toJson(outgoingMessage)
  }

}


object ChatActor {

  def props(
      client: ActorRef,
      userAPI: UserAPI,
      chatMessageAPI: ChatMessageAPI,
      clientId: UUID,
      clientUsername: String,
      timeProvider: TimeProvider,
      uUIDProvider: UUIDProvider) =
    Props(new ChatActor(client, userAPI, chatMessageAPI, clientId, clientUsername, timeProvider, uUIDProvider))

}

