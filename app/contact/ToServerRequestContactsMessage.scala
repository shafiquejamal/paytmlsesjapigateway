package contact

import socket.ToServerSocketMessageType.ToServerRequestContacts
import socket.{SocketMessage, SocketMessageType, ToServerSocketMessageType}

case class ToServerRequestContactsMessage(md5Hash: String) extends SocketMessage {

  override val socketMessageType: SocketMessageType = ToServerRequestContacts

}
