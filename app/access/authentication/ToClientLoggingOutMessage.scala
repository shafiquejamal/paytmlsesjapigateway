package access.authentication

import entrypoint.{SocketMessageType, ToClientNoPayloadMessage}

case object ToClientLoggingOutMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientLoggingOut

  case object ToClientLoggingOut extends SocketMessageType {
    override val description = "SOCKET_LOGGING_OUT"
  }

}
