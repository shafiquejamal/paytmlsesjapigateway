package access.authentication

import socket.SocketMessageType.ToClientLoggingOut
import socket.{SocketMessageType, ToClientNoPayloadMessage}

case object ToClientLoggingOutMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientLoggingOut

}
