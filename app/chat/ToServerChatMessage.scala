package chat

import chat.ToServerSocketMessageType.ToServerChat

case class ToServerChatMessage(recipient: String, text: String) extends SocketMessage {

  override val socketMessageType = ToServerChat

}
