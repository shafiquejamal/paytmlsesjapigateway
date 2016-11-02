package chat

import chat.SocketMessageType.ChatMessage
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}
import scalikejdbc.WrappedResultSet

case class OutgoingChatMessage(
  override val socketMessageType: SocketMessageType,
  from: String,
  to: String,
  text: String,
  time: Long) extends SocketMessage

object OutgoingChatMessage {

  import SocketMessageType.SocketMessageTypeWrites

  implicit val outgoingChatMessageWrites: Writes[OutgoingChatMessage] = (
    (JsPath \ "socketMesageType").write[SocketMessageType] and
    (JsPath \ "from").write[String] and
    (JsPath \ "to").write[String] and
    (JsPath \ "text").write[String] and
    (JsPath \ "time").write[Long]
    )(unlift(OutgoingChatMessage.unapply))

  def converter(rs: WrappedResultSet): OutgoingChatMessage = OutgoingChatMessage(
    ChatMessage,
    rs.string("fromusername"),
    rs.string("tousername"),
    rs.string("messagetext"),
    rs.jodaDateTime("sentat").getMillis
  )

}