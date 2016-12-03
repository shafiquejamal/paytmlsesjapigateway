package access.authentication

import socket.ToServerSocketMessageType.ToServerLogout
import socket.{SocketMessageType, ToServerSocketMessage}

case object ToServerLogoutMessage extends ToServerSocketMessage {

  override def socketMessageType: SocketMessageType = ToServerLogout

}
