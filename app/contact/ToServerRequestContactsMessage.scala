package contact

import chat.ToServerSocketMessageType.ToServerRequestContacts
import chat.{SocketMessage, SocketMessageType}

case class ToServerRequestContactsMessage(md5Hash: String) extends SocketMessage {

  override val socketMessageType: SocketMessageType = ToServerRequestContacts

}
