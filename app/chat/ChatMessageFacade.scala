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

  override def messagesInvolving(userId: UUID, maybeAfter: Option[DateTime]): Seq[ToClientChatMessage] =
    chatMessageDAO.visibleMessages(userId, maybeAfter).map(_.toClientChatMessage).sortBy(_.payload.time)

}
