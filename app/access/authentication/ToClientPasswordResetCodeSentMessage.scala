package access.authentication

import messaging.{SocketMessageType, ToClientNoPayloadMessage}

case object ToClientPasswordResetCodeSentMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientPasswordResetCodeSent

  case object ToClientPasswordResetCodeSent extends SocketMessageType {
    override val description = "PASSWORD_RESET_CODE_SENT"
  }

}
