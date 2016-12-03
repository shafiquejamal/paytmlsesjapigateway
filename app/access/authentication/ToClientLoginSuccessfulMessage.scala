package access.authentication

import socket.SocketMessageType.ToClientLoginSuccessful
import socket.{SocketMessageType, ToClientNoPayloadMessage}

case object ToClientLoginSuccessfulMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientLoginSuccessful

}


