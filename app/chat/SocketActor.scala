package chat

import java.security.MessageDigest
import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import chat.ChatMessageVisibility.Visible
import chat.ToClientChatMessage._
import contact.{ToClientAllContactsMessage, ToServerRequestContactsMessage, ToServerAddContactMessage}
import play.api.libs.json.Json
import user.UserAPI
import util.{TimeProvider, UUIDProvider}
import scala.util._

import scala.concurrent.Future

class SocketActor(
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

  import play.api.libs.json.JsValue
  import scala.concurrent.ExecutionContext.Implicits.global


  override def receive = {
    case msg: JsValue =>

      val messageType = (msg \ "messageType").validate[String].getOrElse("")
      val socketMessage = ToServerSocketMessageType.from(messageType).socketMessage(msg)
      self ! socketMessage

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

    case toClientChatMessage : ToClientChatMessage =>

      client ! Json.toJson(toClientChatMessage)

    case toServerRequestMessagesMessage: ToServerRequestMessagesMessage =>

      client ! Json.toJson(
        ToClientMessagesSinceMessage(chatMessageAPI.messagesInvolving(clientId, toServerRequestMessagesMessage.maybeSince)))

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

    case toClientAllContactsMessage: ToClientAllContactsMessage =>

      client ! Json.toJson(toClientAllContactsMessage)

  }
}

object SocketActor {

  def props(
      client: ActorRef,
      userAPI: UserAPI,
      chatMessageAPI: ChatMessageAPI,
      chatContactsAPI: ChatContactAPI,
      clientId: UUID,
      clientUsername: String,
      timeProvider: TimeProvider,
      uUIDProvider: UUIDProvider) =
    Props(new SocketActor(client, userAPI, chatMessageAPI, chatContactsAPI, clientId, clientUsername, timeProvider, uUIDProvider))

}

