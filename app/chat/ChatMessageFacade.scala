package chat

import java.util.UUID

import com.google.inject.Inject
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
    chatMessageDAO.visibleMessages(userId).map(_.toClientChatMessage)

}
