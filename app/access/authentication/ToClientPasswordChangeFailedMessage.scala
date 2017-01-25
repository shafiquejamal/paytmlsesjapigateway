package access.authentication

import messaging.{SocketMessageType, ToClientNoPayloadMessage}

case object ToClientPasswordChangeFailedMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientPasswordChangeFailed

  case object ToClientPasswordChangeFailed extends SocketMessageType {
    override val description = "PASSWORD_CHANGE_FAILED"
  }

}