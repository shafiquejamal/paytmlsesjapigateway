package access.authentication

import entrypoint.ToServerSocketMessageType.ToServerLogout
import entrypoint.{SocketMessageType, ToServerSocketMessage}

case object ToServerLogoutMessage extends ToServerSocketMessage {

  override def socketMessageType: SocketMessageType = ToServerLogout

}
