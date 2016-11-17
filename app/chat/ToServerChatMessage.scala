package chat

import socket.ToServerSocketMessage
import socket.ToServerSocketMessageType.ToServerChat

case class ToServerChatMessage(recipient: String, text: String) extends ToServerSocketMessage {

  override val socketMessageType = ToServerChat

}
