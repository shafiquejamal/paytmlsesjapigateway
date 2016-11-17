package chat

import org.joda.time.DateTime
import socket.ToServerSocketMessageType.ToServerRequestMessages
import socket.{SocketMessageType, ToServerSocketMessage}

case class ToServerRequestMessagesMessage(maybeSince: Option[DateTime]) extends ToServerSocketMessage {

  override val socketMessageType: SocketMessageType = ToServerRequestMessages
  
}
