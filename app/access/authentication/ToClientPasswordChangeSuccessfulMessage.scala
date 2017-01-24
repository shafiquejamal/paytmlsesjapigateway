package access.authentication

import communication.{SocketMessageType, ToClientNoPayloadMessage}

object ToClientPasswordChangeSuccessfulMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientPasswordChangeSuccessful

  case object ToClientPasswordChangeSuccessful extends SocketMessageType {
    override val description = "PASSWORD_CHANGE_SUCCESSFUL"
  }

}