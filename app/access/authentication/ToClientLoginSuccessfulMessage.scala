package access.authentication

import entrypoint.{SocketMessageType, ToClientNoPayloadMessage}

case object ToClientLoginSuccessfulMessage extends ToClientNoPayloadMessage {

  override val socketMessageType: SocketMessageType = ToClientLoginSuccessful

  case object ToClientLoginSuccessful extends SocketMessageType {
    override val description = "SOCKET_LOGIN_SUCCESSFUL"
  }

}


