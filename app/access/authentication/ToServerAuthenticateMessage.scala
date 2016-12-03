package access.authentication

import socket.ToServerSocketMessageType.ToServerAuthenticate
import socket.{SocketMessageType, ToServerSocketMessage}

case class ToServerAuthenticateMessage(jwt: String) extends ToServerSocketMessage {

  override def socketMessageType: SocketMessageType = ToServerAuthenticate

}
