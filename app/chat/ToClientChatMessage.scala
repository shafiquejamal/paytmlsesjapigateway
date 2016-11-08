package chat

import java.util.UUID

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsValue, Json, Writes}
import socket.SocketMessageType.ToClientChat
import socket.{SocketMessageType, ToClientSocketMessage}

case class ToClientChatMessage(override val payload: Chat) extends ToClientSocketMessage {

  import ToClientChatMessage._

  override val socketMessageType: SocketMessageType = ToClientChat

  override def toJson: JsValue = Json.toJson(this)

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