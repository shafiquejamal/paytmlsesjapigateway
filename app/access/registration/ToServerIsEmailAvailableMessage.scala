package access.registration

import access.authentication.EmailMessage
import messaging.{SocketMessageType, ToServerSocketMessage, ToServerSocketMessageType}
import play.api.libs.json.JsValue

case class ToServerIsEmailAvailableMessage(email: String) extends ToServerSocketMessage {

  override val socketMessageType: SocketMessageType = ToServerIsEmailAvailableMessage.ToServerIsEmailAvailable

}

object ToServerIsEmailAvailableMessage {

  case object ToServerIsEmailAvailable extends ToServerSocketMessageType {
    override val description = "toServerIsEmailAvailable"

    override def socketMessage(msg: JsValue): ToServerIsEmailAvailableMessage =
      EmailMessage.emailMessageReads.reads(msg).asOpt
      .map(message => ToServerIsEmailAvailableMessage(message.email))
      .getOrElse(ToServerIsEmailAvailableMessage(""))
  }

}