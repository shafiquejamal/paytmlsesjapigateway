package access.authentication

import entrypoint.SocketMessageType.ToClientLoggingOut
import entrypoint.{SocketMessageType, ToClientNoPayloadMessage}

case object ToClientLoggingOutMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientLoggingOut

}
