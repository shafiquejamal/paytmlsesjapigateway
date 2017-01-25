package access.registration

import communication.{SocketMessageType, ToClientNoPayloadMessage}

object ToClientAccountActivationSuccessfulMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientAccountActivationSuccessful

  case object ToClientAccountActivationSuccessful extends SocketMessageType {
    override val description = "ACCOUNT_ACTIVATION_SUCCESSFUL"
  }

}
