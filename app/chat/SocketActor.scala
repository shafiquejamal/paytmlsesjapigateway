package chat

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import chat.ChatMessageVisibility.Visible
import chat.ToClientChatMessage._
import play.api.libs.json.Json
import user.UserAPI
import util.{TimeProvider, UUIDProvider}

import scala.concurrent.Future

class SocketActor(
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

  import scala.concurrent.ExecutionContext.Implicits.global

  var chatContacts: Map[String, UUID] = Map()

  override def receive = {
    case msg: JsValue =>

      val messageType = (msg \ "messageType").validate[String].getOrElse("")
      val socketMessage = ToServerSocketMessageType.from(messageType).socketMessage(msg)
      self ! socketMessage

    case ToServerChatMessage(recipient, messageText) =>

      val maybeRecipientIdFromCache = chatContacts.get(recipient)
      val maybeRecipientId = maybeRecipientIdFromCache.orElse(userAPI.by(recipient))
      maybeRecipientId foreach { recipientId =>
        if (maybeRecipientIdFromCache.isEmpty) chatContacts = chatContacts + (recipient -> recipientId)
        val toClientChatMessage =
          ToClientChatMessage(uUIDProvider.randomUUID(), clientUsername, recipient, messageText, timeProvider.now().getMillis)
        Future(
          chatMessageAPI
          .store(
            OutgoingChatMessageWithVisibility(
              toClientChatMessage,
              Visible,
              Visible,
              clientId,
              recipientId)))
        val actorSelectionRecipients = context.actorSelection(s"/user/${recipientId.toString}*")
        actorSelectionRecipients ! toClientChatMessage
        val actorSelectionSenders = context.actorSelection(s"/user/${clientId.toString}*")
        actorSelectionSenders ! toClientChatMessage
      }

    case toClientChatMessage : ToClientChatMessage =>

      client ! Json.toJson(toClientChatMessage)

    case toServerRequestMessagesMessage: ToServerRequestMessagesMessage =>

      client ! Json.toJson(chatMessageAPI.messagesInvolving(clientId, toServerRequestMessagesMessage.maybeSince))
  }
}

object SocketActor {

  def props(
      client: ActorRef,
      userAPI: UserAPI,
      chatMessageAPI: ChatMessageAPI,
      clientId: UUID,
      clientUsername: String,
      timeProvider: TimeProvider,
      uUIDProvider: UUIDProvider) =
    Props(new SocketActor(client, userAPI, chatMessageAPI, clientId, clientUsername, timeProvider, uUIDProvider))

}

