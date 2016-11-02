package chat

import java.util.UUID

import chat.SocketMessageType.ToClientChat
import scalikejdbc.WrappedResultSet

case class OutgoingChatMessageWithVisibility(
    toClientChatMessage: ToClientChatMessage, visibility: ChatMessageVisibility, fromId: UUID, toId: UUID, messageId: UUID)

object OutgoingChatMessageWithVisibility {
  def converter(rs: WrappedResultSet) = OutgoingChatMessageWithVisibility(
    ToClientChatMessage(
      ToClientChat,
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
