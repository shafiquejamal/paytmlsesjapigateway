package access.authentication

import messaging.{SocketMessageType, ToClientNoPayloadMessage}

case object ToClientLoginFailedMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientLoginFailed

  case object ToClientLoginFailed extends SocketMessageType {
    override val description = "LOGIN_FAILED"
  }

}
