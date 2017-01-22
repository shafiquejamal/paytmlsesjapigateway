package access.authentication

import access.authentication.ToServerAuthenticateMessage.ToServerAuthenticate
import communication.{SocketMessageType, ToServerSocketMessage, ToServerSocketMessageType}
import play.api.libs.json.JsValue

case class ToServerAuthenticateMessage(jwt: String) extends ToServerSocketMessage {

  override def socketMessageType: SocketMessageType = ToServerAuthenticate

}

object ToServerAuthenticateMessage {

  case object ToServerAuthenticate extends ToServerSocketMessageType {
    override val description = "toServerAuthenticate"

    override def socketMessage(msg: JsValue): ToServerAuthenticateMessage = ToServerAuthenticateMessage(
       (msg \ "jwt").validate[String].getOrElse("")
    )
  }

}
