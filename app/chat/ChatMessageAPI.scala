package chat

import java.util.UUID

import org.joda.time.DateTime

import scala.util.Try

trait ChatMessageAPI {

  def store(chatMessage: OutgoingChatMessageWithVisibility): Try[ToClientChatMessage]

  def messagesInvolving(userId: UUID): Seq[ToClientChatMessage]

  def messagesInvolving(userId: UUID, after: DateTime): Seq[ToClientChatMessage]

}
