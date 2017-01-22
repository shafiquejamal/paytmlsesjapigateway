package access.authentication

import communication.{SocketMessageType, ToServerSocketMessage, ToServerSocketMessageType}
import play.api.libs.json.JsValue

case object ToServerLogoutMessage extends ToServerSocketMessage {

  override def socketMessageType: SocketMessageType = ToServerLogout

  case object ToServerLogout extends ToServerSocketMessageType {
    override val description = "toServerLogout"

    override def socketMessage(msg: JsValue) = ToServerLogoutMessage
  }

}
