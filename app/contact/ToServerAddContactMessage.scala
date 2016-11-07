package contact

import socket.ToServerSocketMessageType.ToServerAddContact
import socket.{SocketMessage, SocketMessageType}

case class ToServerAddContactMessage(usernameOfContactToAdd: String) extends SocketMessage {

  override def socketMessageType: SocketMessageType = ToServerAddContact

}
