package chat

import chat.SocketMessageType.ToClientChat
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}
import scalikejdbc.WrappedResultSet

case class ToClientChatMessage(
    from: String,
    to: String,
    text: String,
    time: Long) extends SocketMessage {

  override val socketMessageType: SocketMessageType = ToClientChat

}

object ToClientChatMessage {

  import SocketMessageType.SocketMessageTypeWrites

  implicit val outgoingChatMessageWrites: Writes[ToClientChatMessage] = (
    (JsPath \ "socketMesageType").write[SocketMessageType] and
    (JsPath \ "from").write[String] and
    (JsPath \ "to").write[String] and
    (JsPath \ "text").write[String] and
    (JsPath \ "time").write[Long]
    ) ( message => (message.socketMessageType, message.from, message.to, message.text, message.time) )

  def converter(rs: WrappedResultSet): ToClientChatMessage = ToClientChatMessage(
    rs.string("fromusername"),
    rs.string("tousername"),
    rs.string("messagetext"),
    rs.jodaDateTime("sentat").getMillis
  )

}