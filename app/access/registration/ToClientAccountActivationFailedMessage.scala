package access.registration

import messaging.{SocketMessageType, ToClientSocketMessage}
import play.api.libs.json.{JsValue, Json, Writes}

case class ToClientAccountActivationFailedMessage(override val payload: String) extends ToClientSocketMessage {

  override val socketMessageType: SocketMessageType = ToClientAccountActivationFailed

  case object ToClientAccountActivationFailed extends SocketMessageType {
    override val description = "ACTIVATION_FAILED"
  }

  import ToClientAccountActivationFailedMessage.toClientAccountActivationFailedMessageWrites
  override def toJson: JsValue = Json.toJson(this)

}

object ToClientAccountActivationFailedMessage {

  implicit val toClientAccountActivationFailedMessageWrites: Writes[ToClientAccountActivationFailedMessage] =
    new Writes[ToClientAccountActivationFailedMessage] {
      def writes(toClientAccountActivationFailedMessage: ToClientAccountActivationFailedMessage) = Json.obj(
        "socketMessageType" -> toClientAccountActivationFailedMessage.socketMessageType,
        "payload" -> toClientAccountActivationFailedMessage.payload
      )
    }
}