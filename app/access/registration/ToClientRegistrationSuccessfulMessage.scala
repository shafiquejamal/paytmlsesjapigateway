package access.registration

import communication.{SocketMessageType, ToClientNoPayloadMessage}

object ToClientRegistrationSuccessfulMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientRegistrationSuccessful

  case object ToClientRegistrationSuccessful extends SocketMessageType {
    override val description = "REGISTRATION_SUCCESSFUL"
  }

}