package chat

import java.util.UUID

import org.joda.time.DateTime

import scala.util.Try

trait ChatMessageDAO {

  def add(
      chatMessage: OutgoingChatMessageWithVisibility,
      chatMessageUUID: UUID,
      createdAt: DateTime,
      visibilityUUID: UUID): Try[OutgoingChatMessageWithVisibility]

  def addSenderVisibility(
      chatMessageUUID: UUID,
      createdAt: DateTime,
      senderVisibility: ChatMessageVisibility,
      visibilityUUID: UUID): Try[UUID]

  def addReceiverVisibility(
      chatMessageUUID: UUID,
      createdAt: DateTime,
      receiverVisibility: ChatMessageVisibility,
      visibilityUUID: UUID): Try[UUID]

  def visibleMessages(toOrFromXuserId: UUID): Seq[OutgoingChatMessageWithVisibility]
}
