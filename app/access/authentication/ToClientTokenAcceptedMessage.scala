package access.authentication

import communication.{SocketMessageType, ToClientNoPayloadMessage}

case object ToClientTokenAcceptedMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientLoginSuccessful

  case object ToClientLoginSuccessful extends SocketMessageType {
    override val description = "SOCKET_TOKEN_ACCEPTED"
  }

}


