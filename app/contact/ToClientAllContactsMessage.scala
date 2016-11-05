package contact

import chat.SocketMessageType.ToClientAllContacts
import chat.{SocketMessage, SocketMessageType}
import play.api.libs.json.{Json, Writes}

case class ToClientAllContactsMessage(payload: Seq[String]) extends SocketMessage {

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