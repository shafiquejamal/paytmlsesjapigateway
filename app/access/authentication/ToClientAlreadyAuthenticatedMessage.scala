package access.authentication

import entrypoint.SocketMessageType.ToClientAlreadyAuthenticated
import entrypoint.{SocketMessageType, ToClientNoPayloadMessage}

case object ToClientAlreadyAuthenticatedMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientAlreadyAuthenticated

}
