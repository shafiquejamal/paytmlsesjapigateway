package access.authentication

import communication.{ToServerSocketMessageType, SocketMessageType, ToServerSocketMessage}
import play.api.libs.json.JsValue

case object ToServerLogoutAllMessage extends ToServerSocketMessage {

  override def socketMessageType: SocketMessageType = ToServerLogoutAll

  case object ToServerLogoutAll extends ToServerSocketMessageType {
    override val description = "toServerLogoutAll"

    override def socketMessage(msg: JsValue) = ToServerLogoutAllMessage
  }

}
