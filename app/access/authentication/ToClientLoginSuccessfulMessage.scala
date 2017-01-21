package access.authentication

import entrypoint.SocketMessageType.ToClientLoginSuccessful
import entrypoint.{SocketMessageType, ToClientNoPayloadMessage}

case object ToClientLoginSuccessfulMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientLoginSuccessful

}


