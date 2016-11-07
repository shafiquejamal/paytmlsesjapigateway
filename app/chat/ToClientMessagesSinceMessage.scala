package chat

import play.api.libs.json.{Json, Writes}
import socket.SocketMessageType.ToClientMessagesSince
import socket.{SocketMessageType, ToClientSocketMessage}

case class ToClientMessagesSinceMessage(override val payload: Seq[ToClientChatMessage]) extends ToClientSocketMessage {

  override val socketMessageType: SocketMessageType = ToClientMessagesSince

}

object ToClientMessagesSinceMessage {

  import SocketMessageType.SocketMessageTypeWrites
  import ToClientChatMessage.chatWries

  implicit val toClientMessagesSinceMessageWrites = new Writes[ToClientMessagesSinceMessage] {
    def writes(toClientMessagesSinceMessage: ToClientMessagesSinceMessage) = Json.obj(
      "payload" -> toClientMessagesSinceMessage.payload.map(_.payload),
      "socketMessageType" -> toClientMessagesSinceMessage.socketMessageType
    )
  }

}
