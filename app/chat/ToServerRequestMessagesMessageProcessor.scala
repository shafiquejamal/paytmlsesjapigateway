package chat

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class ToServerRequestMessagesMessageProcessor(
    client: ActorRef,
    chatMessageAPI: ChatMessageAPI,
    clientId: UUID)
  extends Actor
  with ActorLogging {

  override def receive = {

    case toServerRequestMessagesMessage: ToServerRequestMessagesMessage =>
      val messages = chatMessageAPI.messagesInvolving(clientId, toServerRequestMessagesMessage.maybeSince)
      client ! ToClientMessagesSinceMessage(messages)

  }

}

object ToServerRequestMessagesMessageProcessor {

  def props(client: ActorRef, chatMessageAPI: ChatMessageAPI, clientId: UUID) =
    Props(new ToServerRequestMessagesMessageProcessor(client, chatMessageAPI, clientId))

}