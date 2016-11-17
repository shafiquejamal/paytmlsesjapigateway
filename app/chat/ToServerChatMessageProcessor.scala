package chat

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import chat.ChatMessageVisibility.Visible
import user.UserAPI
import util.{TimeProvider, UUIDProvider}

import scala.concurrent.Future

class ToServerChatMessageProcessor(
    client: ActorRef,
    userAPI: UserAPI,
    chatMessageAPI: ChatMessageAPI,
    clientId: UUID,
    clientUsername: String,
    timeProvider: TimeProvider,
    uUIDProvider: UUIDProvider)
  extends Actor
  with ActorLogging {

  import scala.concurrent.ExecutionContext.Implicits.global

  override def receive = {

    case ToServerChatMessage(recipient, messageText) =>

      userAPI.by(recipient) foreach { recipientId =>
        val toClientChatMessage =
          ToClientChatMessage(
          Chat(uUIDProvider.randomUUID(), clientUsername, recipient, messageText, timeProvider.now().getMillis))
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
        val actorSelectionSenderConnections = context.actorSelection(s"/user/${clientId.toString}*")
        actorSelectionSenderConnections ! toClientChatMessage
      }

  }
}

object ToServerChatMessageProcessor {

   def props(
    client: ActorRef,
    userAPI: UserAPI,
    chatMessageAPI: ChatMessageAPI,
    clientId: UUID,
    clientUsername: String,
    timeProvider: TimeProvider,
    uUIDProvider: UUIDProvider) =
      Props(
        new ToServerChatMessageProcessor(
          client, userAPI, chatMessageAPI, clientId, clientUsername, timeProvider, uUIDProvider))

}


