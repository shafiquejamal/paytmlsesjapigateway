package chat

import java.util.UUID

import chat.SocketMessageType.ToClientChat
import scalikejdbc.WrappedResultSet

case class OutgoingChatMessageWithVisibility(
    toClientChatMessage: ToClientChatMessage,
    senderVisibility: ChatMessageVisibility,
    receiverVisibility: ChatMessageVisibility,
    fromId: UUID,
    toId: UUID,
    messageId: UUID)

object OutgoingChatMessageWithVisibility {
  def converter(rs: WrappedResultSet) = OutgoingChatMessageWithVisibility(
    ToClientChatMessage(
      ToClientChat,
      rs.string("fromusername"),
      rs.string("tousername"),
      rs.string("messagetext"),
      rs.jodaDateTime("sentat").getMillis),
    ChatMessageVisibility.from(rs.int("sendervisibility")),
    ChatMessageVisibility.from(rs.int("receivervisibility")),
    UUID.fromString(rs.string("fromxuserid")),
    UUID.fromString(rs.string("toxuserid")),
    UUID.fromString(rs.string("chatmsgid"))
  )
}
