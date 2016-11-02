package chat

import java.util.UUID

import play.api.libs.json._
import play.api.libs.functional.syntax._
import scalikejdbc.WrappedResultSet

sealed trait ChatMessageVisibility { def number: Int }
case object Both extends ChatMessageVisibility { override val number = 3 }
case object SenderOnly extends ChatMessageVisibility { override val number = 1 }
case object ReceiverOnly extends ChatMessageVisibility { override val number = 2 }
case object Neither extends ChatMessageVisibility { override val number = 0 }
object ChatMessageVisibility {
  val all = Seq(Both, SenderOnly, ReceiverOnly, Neither)
  def from(number: Int) = all.find(_.number == number).getOrElse(Neither)
}

case class OutgoingChatMessageWithVisibility(
    outgoingChatMessage: OutgoingChatMessage, visibility: ChatMessageVisibility, fromId: UUID, toId: UUID, messageId: UUID)

object OutgoingChatMessageWithVisibility {
  def converter(rs: WrappedResultSet) = OutgoingChatMessageWithVisibility(
    OutgoingChatMessage(rs.string("fromusername"), rs.string("tousername"), rs.string("messagetext"), rs.jodaDateTime("sentat").getMillis),
    ChatMessageVisibility.from(rs.int("visibility")),
    UUID.fromString(rs.string("fromxuserid")),
    UUID.fromString(rs.string("toxuserid")),
    UUID.fromString(rs.string("chatmessageid"))
  )
}

case class OutgoingChatMessage(from: String, to: String, text: String, time: Long)

object OutgoingChatMessage {

  implicit val outgoingChatMessageWrites: Writes[OutgoingChatMessage] = (
    (JsPath \ "from").write[String] and
    (JsPath \ "to").write[String] and
    (JsPath \ "text").write[String] and
    (JsPath \ "time").write[Long]
  )(unlift(OutgoingChatMessage.unapply))

  def converter(rs: WrappedResultSet): OutgoingChatMessage = OutgoingChatMessage(
    rs.string("fromusername"), rs.string("tousername"), rs.string("messagetext"), rs.jodaDateTime("sentat").getMillis
  )

}