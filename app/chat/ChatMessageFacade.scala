package chat

import java.util.UUID

import com.google.inject.Inject
import org.joda.time.DateTime
import util.{TimeProvider, UUIDProvider}

import scala.util.Try

class ChatMessageFacade @Inject() (
    chatMessageDAO: ChatMessageDAO,
    uUIDProvider: UUIDProvider,
    timeProvider: TimeProvider) extends ChatMessageAPI {

  override def store(chatMessage: OutgoingChatMessageWithVisibility): Try[ToClientChatMessage] =
    chatMessageDAO
    .add(chatMessage, uUIDProvider.randomUUID(), timeProvider.now(), uUIDProvider.randomUUID()).map(_.toClientChatMessage)

  override def messagesInvolving(userId: UUID): Seq[ToClientChatMessage] =
    chatMessageDAO.visibleMessages(userId, None).map(_.toClientChatMessage)

  def messagesInvolving(userId: UUID, after: DateTime): Seq[ToClientChatMessage] =
    chatMessageDAO.visibleMessages(userId, Some(after)).map(_.toClientChatMessage)

}
