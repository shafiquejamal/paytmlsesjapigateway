package chat

import chat.SocketMessageType.ToClientMessagesSince
import play.api.libs.json.{Json, Writes}

case class ToClientMessagesSinceMessage(payload: Seq[ToClientChatMessage]) extends SocketMessage {

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
