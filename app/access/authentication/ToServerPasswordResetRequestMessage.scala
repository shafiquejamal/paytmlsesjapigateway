package access.authentication

import access.authentication.ToServerPasswordResetRequestMessage.ToServerPasswordResetRequest
import entrypoint.{SocketMessageType, ToServerSocketMessage, ToServerSocketMessageType}
import play.api.libs.json.JsValue

case class ToServerPasswordResetRequestMessage(email: String) extends ToServerSocketMessage {

  override def socketMessageType: SocketMessageType = ToServerPasswordResetRequest

}

object ToServerPasswordResetRequestMessage {

  case object ToServerPasswordResetRequest extends ToServerSocketMessageType {
    override val description = "toServerPasswordResetRequest"

    override def socketMessage(msg: JsValue): ToServerPasswordResetRequestMessage =
      EmailMessage.emailMessageReads.reads(msg).asOpt
      .map(message => ToServerPasswordResetRequestMessage(message.email))
      .getOrElse(ToServerPasswordResetRequestMessage(""))
  }

}