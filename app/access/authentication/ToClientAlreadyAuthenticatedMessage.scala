package access.authentication

import entrypoint.{SocketMessageType, ToClientNoPayloadMessage}

case object ToClientAlreadyAuthenticatedMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientAlreadyAuthenticated

  case object ToClientAlreadyAuthenticated extends SocketMessageType {
    override val description = "SOCKET_ALREADY_AUTHENTICATED"
  }

}
