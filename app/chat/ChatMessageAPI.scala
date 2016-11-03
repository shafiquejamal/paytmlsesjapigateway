package chat

import java.util.UUID

import org.joda.time.DateTime

import scala.util.Try

trait ChatMessageAPI {

  def store(chatMessage: OutgoingChatMessageWithVisibility): Try[ToClientChatMessage]

  def messagesInvolving(userId: UUID, maybeAfter: Option[DateTime]): Seq[ToClientChatMessage]

}
