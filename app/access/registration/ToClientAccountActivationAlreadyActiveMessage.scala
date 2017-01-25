package access.registration

import messaging.{SocketMessageType, ToClientNoPayloadMessage}

object ToClientAccountActivationAlreadyActiveMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientAccountAlreadyActive

  case object ToClientAccountAlreadyActive extends SocketMessageType {
    override val description = "ACCOUNT_ALREADY_ACTIVE"
  }

}