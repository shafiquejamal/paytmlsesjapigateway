package chat

import java.util.UUID

import scalikejdbc.WrappedResultSet

case class OutgoingChatMessageWithVisibility(
    toClientChatMessage: ToClientChatMessage,
    senderVisibility: ChatMessageVisibility,
    receiverVisibility: ChatMessageVisibility,
    fromId: UUID,
    toId: UUID)

object OutgoingChatMessageWithVisibility {
  def converter(rs: WrappedResultSet) = OutgoingChatMessageWithVisibility(
    ToClientChatMessage(Chat(
      UUID.fromString(rs.string("chatmsgid")),
      rs.string("fromusername"),
      rs.string("tousername"),
      rs.string("messagetext"),
      rs.jodaDateTime("sentat").getMillis)),
    ChatMessageVisibility.from(rs.int("sendervisibility")),
    ChatMessageVisibility.from(rs.int("receivervisibility")),
    UUID.fromString(rs.string("fromxuserid")),
    UUID.fromString(rs.string("toxuserid"))
  )
}
