package access.registration

import access.authentication.EmailMessage
import communication.{SocketMessageType, ToServerSocketMessage, ToServerSocketMessageType}
import play.api.libs.json.JsValue

case class ToServerResendActivationCodeMessage(email: String) extends ToServerSocketMessage {

  override val socketMessageType: SocketMessageType = ToServerResendActivationCodeMessage.ToServerResendActivationCode

}

object ToServerResendActivationCodeMessage {

  case object ToServerResendActivationCode extends ToServerSocketMessageType {
    override val description = "toServerResendActivationCode"

    override def socketMessage(msg: JsValue): ToServerResendActivationCodeMessage =
      EmailMessage.emailMessageReads.reads(msg).asOpt
      .map(message => ToServerResendActivationCodeMessage(message.email))
      .getOrElse(ToServerResendActivationCodeMessage(""))
  }

}