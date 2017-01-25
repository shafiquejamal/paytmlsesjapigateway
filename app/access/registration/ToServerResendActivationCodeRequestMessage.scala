package access.registration

import access.authentication.EmailMessage
import access.registration.ToServerResendActivationCodeRequestMessage.ToServerResendActivationCodeRequest
import messaging.{SocketMessageType, ToServerSocketMessage, ToServerSocketMessageType}
import play.api.libs.json.JsValue

case class ToServerResendActivationCodeRequestMessage(email: String) extends ToServerSocketMessage {

  override def socketMessageType: SocketMessageType = ToServerResendActivationCodeRequest

}

object ToServerResendActivationCodeRequestMessage {

  case object ToServerResendActivationCodeRequest extends ToServerSocketMessageType {
    override val description = "toServerResendActivationCodeRequest"

    override def socketMessage(msg: JsValue): ToServerResendActivationCodeRequestMessage =
      EmailMessage.emailMessageReads.reads(msg).asOpt
      .map(message => ToServerResendActivationCodeRequestMessage(message.email))
      .getOrElse(ToServerResendActivationCodeRequestMessage(""))
  }

}
