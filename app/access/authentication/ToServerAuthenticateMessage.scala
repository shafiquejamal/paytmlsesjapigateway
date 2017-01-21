package access.authentication

import entrypoint.ToServerSocketMessageType.ToServerAuthenticate
import entrypoint.{SocketMessageType, ToServerSocketMessage}

case class ToServerAuthenticateMessage(jwt: String) extends ToServerSocketMessage {

  override def socketMessageType: SocketMessageType = ToServerAuthenticate

}
