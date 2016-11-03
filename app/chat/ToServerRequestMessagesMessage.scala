package chat

import chat.ToServerSocketMessageType.ToServerRequestMessages
import org.joda.time.DateTime

case class ToServerRequestMessagesMessage(maybeSince: Option[DateTime]) extends SocketMessage {

  override val socketMessageType: SocketMessageType = ToServerRequestMessages
  
}
