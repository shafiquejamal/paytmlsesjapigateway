package access.authentication

import messaging.{SocketMessageType, ToClientNoPayloadMessage}

case object ToClientPasswordResetFailedMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientPasswordResetFailed

  case object ToClientPasswordResetFailed extends SocketMessageType {
    override val description = "PASSWORD_RESET_FAILED"
  }

}