package access.authentication

import communication.{SocketMessageType, ToClientNoPayloadMessage}

object ToClientLogoutMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientLogout

  case object ToClientLogout extends SocketMessageType {
    override val description = "LOGOUT_USER"
  }

}
