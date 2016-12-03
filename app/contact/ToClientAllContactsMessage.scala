package contact

import play.api.libs.json.{JsValue, Json, Writes}
import socket.SocketMessageType.ToClientAllContacts
import socket.{SocketMessageType, ToClientSocketMessage}

case class ToClientAllContactsMessage(override val payload: Seq[String]) extends ToClientSocketMessage {

  import ToClientAllContactsMessage._

  override val socketMessageType: SocketMessageType = ToClientAllContacts
  
  override def toJson: JsValue = Json.toJson(this)

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