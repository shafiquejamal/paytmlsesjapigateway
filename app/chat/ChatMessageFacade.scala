package chat

import java.util.UUID

import com.google.inject.Inject
import util.{TimeProvider, UUIDProvider}

import scala.util.Try

class ChatMessageFacade @Inject() (
    chatMessageDAO: ChatMessageDAO,
    uUIDProvider: UUIDProvider,
    timeProvider: TimeProvider) extends ChatMessageAPI {

  override def store(chatMessage: OutgoingChatMessageWithVisibility): Try[OutgoingChatMessage] =
    chatMessageDAO
    .add(chatMessage, uUIDProvider.randomUUID(), timeProvider.now(), uUIDProvider.randomUUID()).map(_.outgoingChatMessage)

  override def messagesInvolving(userId: UUID): Seq[OutgoingChatMessage] =
    chatMessageDAO.visibleMessages(userId).map(_.outgoingChatMessage)

}
