package contact

import socket.ToServerSocketMessageType.ToServerAddContact
import socket.{SocketMessageType, ToServerSocketMessage}

case class ToServerAddContactMessage(usernameOfContactToAdd: String) extends ToServerSocketMessage {

  override def socketMessageType: SocketMessageType = ToServerAddContact

}
