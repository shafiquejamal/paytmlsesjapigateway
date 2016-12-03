package chat

import java.security.MessageDigest
import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import chat.ClientPaths.namedClientPath
import contact.{ToClientAllContactsMessage, ToServerRequestContactsMessage}

class ToServerRequestContactsMessageProcessor(
    chatContactsAPI: ChatContactAPI,
    clientId: UUID)
  extends Actor
  with ActorLogging {

  override def receive = {

    case toServerRequestContactsMessage: ToServerRequestContactsMessage =>

      val digest = MessageDigest.getInstance("MD5")

      val md5ofContactsOnClient = toServerRequestContactsMessage.md5Hash
      val visibleContactsForThisClient = chatContactsAPI.visibleContactsFor(clientId)
      val contactsOnServerStringified =
        "[" + visibleContactsForThisClient.map(contact => "\"" + contact + "\"").mkString(",") + "]"
      val md5ofContactsOnServer = digest.digest(contactsOnServerStringified.getBytes).map("%02x".format(_)).mkString

      if (md5ofContactsOnClient != md5ofContactsOnServer) {
        val actorSelectionAllClientConnections = context.actorSelection(namedClientPath(clientId))
        actorSelectionAllClientConnections ! ToClientAllContactsMessage(visibleContactsForThisClient)
      }

  }
  
}

object ToServerRequestContactsMessageProcessor {

  def props(chatContactsAPI: ChatContactAPI, clientId: UUID) =
    Props(new ToServerRequestContactsMessageProcessor(chatContactsAPI, clientId))

}
