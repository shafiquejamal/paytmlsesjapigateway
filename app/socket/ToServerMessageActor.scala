package socket

import java.security.MessageDigest
import java.util.UUID

import akka.actor.{Props, Actor, ActorLogging, ActorRef}
import chat.ChatMessageVisibility.Visible
import chat._
import contact.{ToClientAllContactsMessage, ToServerAddContactMessage, ToServerRequestContactsMessage}
import user.UserAPI
import util.{TimeProvider, UUIDProvider}

import scala.concurrent.Future
import scala.util.{Failure, Success}

class ToServerMessageActor(
    client: ActorRef,
    userAPI: UserAPI,
    chatMessageAPI: ChatMessageAPI,
    chatContactsAPI: ChatContactAPI,
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

    case toServerRequestMessagesMessage: ToServerRequestMessagesMessage =>

      client ! ToClientMessagesSinceMessage(chatMessageAPI.messagesInvolving(clientId, toServerRequestMessagesMessage.maybeSince)).toJson

    case toServerRequestContactsMessage: ToServerRequestContactsMessage =>

      val digest = MessageDigest.getInstance("MD5")

      val md5ofContactsOnClient = toServerRequestContactsMessage.md5Hash
      val visibleContactsForThisClient = chatContactsAPI.visibleContactsFor(clientId)
      val contactsOnServerStringified =
        "[" + visibleContactsForThisClient.map(contact => "\"" + contact + "\"").mkString(",") + "]"
      val md5ofContactsOnServer = digest.digest(contactsOnServerStringified.getBytes).map("%02x".format(_)).mkString

      if (md5ofContactsOnClient != md5ofContactsOnServer) {
        val actorSelectionAllClientConnections = context.actorSelection(s"/user/${clientId.toString}*")
        actorSelectionAllClientConnections ! ToClientAllContactsMessage(visibleContactsForThisClient)
      }

    case toServerAddContactMessage: ToServerAddContactMessage =>

      val maybeUserIdOfContactToAdd = userAPI.by(toServerAddContactMessage.usernameOfContactToAdd)
      maybeUserIdOfContactToAdd foreach { userIdOfContactToAdd =>
        chatContactsAPI.addContact(clientId, userIdOfContactToAdd) match {
          case Success(uUID) =>
            self ! ToServerRequestContactsMessage("")
          case Failure(_) =>
        }
      }

  }

}

object ToServerMessageActor {

  def props(
    client: ActorRef,
    userAPI: UserAPI,
    chatMessageAPI: ChatMessageAPI,
    chatContactsAPI: ChatContactAPI,
    clientId: UUID,
    clientUsername: String,
    timeProvider: TimeProvider,
    uUIDProvider: UUIDProvider) =
      Props(
        new ToServerMessageActor(
          client, userAPI, chatMessageAPI, chatContactsAPI, clientId, clientUsername, timeProvider, uUIDProvider))

}