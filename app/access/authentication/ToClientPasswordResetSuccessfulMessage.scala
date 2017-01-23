package access.authentication

import communication.{SocketMessageType, ToClientNoPayloadMessage}

case object ToClientPasswordResetSuccessfulMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientPasswordResetSuccessful

  case object ToClientPasswordResetSuccessful extends SocketMessageType {
    override val description = "PASSWORD_RESET_SUCCESSFUL"
  }

}