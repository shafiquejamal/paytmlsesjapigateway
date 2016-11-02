package chat

import java.util.UUID

import chat.SocketMessageType.ChatMessage
import scalikejdbc.WrappedResultSet

case class OutgoingChatMessageWithVisibility(
    outgoingChatMessage: OutgoingChatMessage, visibility: ChatMessageVisibility, fromId: UUID, toId: UUID, messageId: UUID)

object OutgoingChatMessageWithVisibility {
  def converter(rs: WrappedResultSet) = OutgoingChatMessageWithVisibility(
    OutgoingChatMessage(
      ChatMessage,
      rs.string("fromusername"),
      rs.string("tousername"),
      rs.string("messagetext"),
      rs.jodaDateTime("sentat").getMillis),
    ChatMessageVisibility.from(rs.int("visibility")),
    UUID.fromString(rs.string("fromxuserid")),
    UUID.fromString(rs.string("toxuserid")),
    UUID.fromString(rs.string("chatmessageid"))
  )
}
