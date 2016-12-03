package access.authentication

import socket.SocketMessageType._
import socket.{SocketMessageType, ToClientNoPayloadMessage}

case object ToClientAlreadyAuthenticatedMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientAlreadyAuthenticated

}
