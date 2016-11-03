package chat

import chat.SocketMessageType.ToClientMessagesSince
import play.api.libs.json.{Json, Writes}

case class ToClientMessagesSinceMessage(toClientChatMessages: Seq[ToClientChatMessage]) extends SocketMessage {

  override val socketMessageType: SocketMessageType = ToClientMessagesSince

}

object ToClientMessagesSinceMessage {

  import SocketMessageType.SocketMessageTypeWrites

  implicit val toClientMessagesSinceMessageWrites = new Writes[ToClientMessagesSinceMessage] {
    def writes(toClientMessagesSinceMessage: ToClientMessagesSinceMessage) = Json.obj(
      "toClientChatMessages" -> toClientMessagesSinceMessage.toClientChatMessages,
      "socketMessageType" -> toClientMessagesSinceMessage.socketMessageType
    )
  }

}
