package contact

import play.api.libs.json.{Json, Writes}
import socket.SocketMessageType.ToClientAllContacts
import socket.{SocketMessageType, ToClientSocketMessage}

case class ToClientAllContactsMessage(override val payload: Seq[String]) extends ToClientSocketMessage {

  override val socketMessageType: SocketMessageType = ToClientAllContacts

}

object ToClientAllContactsMessage {

  import SocketMessageType.SocketMessageTypeWrites

  implicit val toClientAllContactsMessageWrites: Writes[ToClientAllContactsMessage] =
    new Writes[ToClientAllContactsMessage] {
      def writes(toClientAllContactsMessage: ToClientAllContactsMessage) = Json.obj(
        "payload" -> toClientAllContactsMessage.payload,
        "socketMessageType" -> toClientAllContactsMessage.socketMessageType
      )
  }

}