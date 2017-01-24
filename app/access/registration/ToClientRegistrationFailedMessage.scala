package access.registration

import communication.{SocketMessageType, ToClientNoPayloadMessage}

object ToClientRegistrationFailedMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientRegistrationFailed

  case object ToClientRegistrationFailed extends SocketMessageType {
    override val description = "REGISTRATION_FAILED"
  }

}