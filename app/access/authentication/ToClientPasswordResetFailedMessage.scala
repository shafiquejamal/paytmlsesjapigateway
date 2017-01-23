package access.authentication

import communication.{SocketMessageType, ToClientNoPayloadMessage}

case object ToClientPasswordResetFailedMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientPasswordResetFailed

  case object ToClientPasswordResetFailed extends SocketMessageType {
    override val description = "PASSWORD_RESET_FAILED"
  }

}