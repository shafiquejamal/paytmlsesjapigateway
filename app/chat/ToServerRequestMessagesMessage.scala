package chat

import org.joda.time.DateTime
import socket.ToServerSocketMessageType.ToServerRequestMessages
import socket.{SocketMessage, SocketMessageType}

case class ToServerRequestMessagesMessage(maybeSince: Option[DateTime]) extends SocketMessage {

  override val socketMessageType: SocketMessageType = ToServerRequestMessages
  
}
