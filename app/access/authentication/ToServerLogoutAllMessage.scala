package access.authentication

import messaging.{SocketMessageType, ToServerSocketMessage, ToServerSocketMessageType}
import play.api.libs.json.JsValue

case object ToServerLogoutAllMessage extends ToServerSocketMessage {

  override def socketMessageType: SocketMessageType = ToServerLogoutAll

  case object ToServerLogoutAll extends ToServerSocketMessageType {
    override val description = "toServerLogoutAll"

    override def socketMessage(msg: JsValue) = ToServerLogoutAllMessage
  }

}
