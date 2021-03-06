package access.authentication

import messaging.{SocketMessageType, ToClientNoPayloadMessage}

case object ToClientTokenAcceptedMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientTokenAccepted

  case object ToClientTokenAccepted extends SocketMessageType {
    override val description = "SOCKET_TOKEN_ACCEPTED"
  }

}


