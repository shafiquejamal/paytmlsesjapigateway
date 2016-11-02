package chat

import java.util.UUID

import scala.util.Try

trait ChatMessageAPI {

  def store(chatMessage: OutgoingChatMessageWithVisibility): Try[ToClientChatMessage]

  def messagesInvolving(userId: UUID): Seq[ToClientChatMessage]

}
