package chat

import chat.SocketMessageType.ToServerChat

case class ToServerChatMessage(recipient: String, text: String) extends SocketMessage {

  override val socketMessageType = ToServerChat

}
