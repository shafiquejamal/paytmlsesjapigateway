package access.authentication

import entrypoint.{SocketMessageType, ToClientNoPayloadMessage}

case object ToClientLoginFailedMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientLoginFailed

  case object ToClientLoginFailed extends SocketMessageType {
    override val description = "SOCKET_LOGIN_FAILED"
  }

}
