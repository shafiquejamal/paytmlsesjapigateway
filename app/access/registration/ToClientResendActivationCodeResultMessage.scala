package access.registration

import communication.{SocketMessageType, ToClientSocketMessage}
import play.api.libs.json.{JsValue, Json, Writes}

case class ToClientResendActivationCodeResultMessage(override val payload: String) extends ToClientSocketMessage {

  override val socketMessageType: SocketMessageType = ToClientResendActivationCodeResult

  case object ToClientResendActivationCodeResult extends SocketMessageType {
    override val description = "RESEND_ACTIVATION_CODE_RESULT"
  }

  import ToClientResendActivationCodeResultMessage.toClientResendActivationCodeResultMessageReads
  override def toJson: JsValue = Json.toJson(this)

}

object ToClientResendActivationCodeResultMessage {

  implicit val toClientResendActivationCodeResultMessageReads: Writes[ToClientResendActivationCodeResultMessage] =
    new Writes[ToClientResendActivationCodeResultMessage] {
      def writes(toClientResendActivationCodeResultMessage: ToClientResendActivationCodeResultMessage) = Json.obj(
        "socketMessageType" -> toClientResendActivationCodeResultMessage.socketMessageType,
        "payload" -> toClientResendActivationCodeResultMessage.payload
      )
    }
}