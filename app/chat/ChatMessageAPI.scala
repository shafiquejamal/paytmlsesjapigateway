package chat

import java.util.UUID

import scala.util.Try

trait ChatMessageAPI {

  def store(chatMessage: OutgoingChatMessageWithVisibility): Try[OutgoingChatMessage]

  def messagesInvolving(userId: UUID): Seq[OutgoingChatMessage]

}
