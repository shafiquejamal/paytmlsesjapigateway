package chat

import java.util.UUID

import chat.SocketMessageType.ToClientChat
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}

case class ToClientChatMessage(override val payload: Chat) extends ToClientSocketMessage {

  override val socketMessageType: SocketMessageType = ToClientChat

}

case class Chat(
    id: UUID,
    from: String,
    to: String,
    text: String,
    time: Long)

object ToClientChatMessage {

  import SocketMessageType.SocketMessageTypeWrites

  implicit val chatWries: Writes[Chat] = (
    (JsPath \ "id").write[UUID] and
    (JsPath \ "from").write[String] and
    (JsPath \ "to").write[String] and
    (JsPath \ "text").write[String] and
    (JsPath \ "time").write[Long]
    )(unlift(Chat.unapply))

  implicit val toClientChatMessageWrites: Writes[ToClientChatMessage] = (

    (JsPath \ "socketMessageType").write[SocketMessageType] and
    (JsPath \ "payload").write[Chat]
    ) ( message => (message.socketMessageType, message.payload))

}