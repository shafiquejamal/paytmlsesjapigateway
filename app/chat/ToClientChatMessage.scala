package chat

import java.util.UUID

import chat.SocketMessageType.ToClientChat
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}

case class ToClientChatMessage(
    id: UUID,
    from: String,
    to: String,
    text: String,
    time: Long) extends SocketMessage {

  override val socketMessageType: SocketMessageType = ToClientChat

}

object ToClientChatMessage {

  import SocketMessageType.SocketMessageTypeWrites

  implicit val toClientChatMessageWrites: Writes[ToClientChatMessage] = (
    (JsPath \ "id").write[UUID] and
    (JsPath \ "socketMesageType").write[SocketMessageType] and
    (JsPath \ "from").write[String] and
    (JsPath \ "to").write[String] and
    (JsPath \ "text").write[String] and
    (JsPath \ "time").write[Long]
    ) ( message => (message.id, message.socketMessageType, message.from, message.to, message.text, message.time) )

}