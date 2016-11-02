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

  def addMessageVisibility(
      chatMessageUUID: UUID,
      createdAt: DateTime,
      visibility: ChatMessageVisibility,
      visibilityUUID: UUID): Try[UUID]

  def visibleMessages(toOrFromXuserId: UUID): Seq[OutgoingChatMessageWithVisibility]
}
