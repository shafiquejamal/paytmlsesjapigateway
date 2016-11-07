package chat

import socket.ToServerSocketMessageType.ToServerChat
import socket.{SocketMessage, ToServerSocketMessageType}

case class ToServerChatMessage(recipient: String, text: String) extends SocketMessage {

  override val socketMessageType = ToServerChat

}
