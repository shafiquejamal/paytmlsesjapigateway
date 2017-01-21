package access.authentication

import entrypoint.SocketMessageType.ToClientLoginFailed
import entrypoint.{SocketMessageType, ToClientNoPayloadMessage}

case object ToClientLoginFailedMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientLoginFailed

}
