package contact

import socket.ToServerSocketMessageType.ToServerRequestContacts
import socket.{SocketMessageType, ToServerSocketMessage}

case class ToServerRequestContactsMessage(md5Hash: String) extends ToServerSocketMessage {

  override val socketMessageType: SocketMessageType = ToServerRequestContacts

}
