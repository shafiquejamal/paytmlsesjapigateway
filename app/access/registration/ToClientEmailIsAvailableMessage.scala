package access.registration

import communication.{SocketMessageType, ToClientSocketMessage}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsValue, Json, Writes}

case class EmailAvailability(email: String, available: Boolean)

object EmailAvailability {

   implicit val emailAvailabilityWrites: Writes[EmailAvailability] = (
    (JsPath \ "email").write[String] and
    (JsPath \ "available").write[Boolean]
    ) ( message => (message.email, message.available))

}

case class ToClientEmailIsAvailableMessage(override val payload: EmailAvailability) extends ToClientSocketMessage {

  override val socketMessageType: SocketMessageType = ToClientEmailIsAvailableMessage.ToClientEmailIsAvailable

  import ToClientEmailIsAvailableMessage.toClientEmailIsAvailableMessageWrites
  override def toJson: JsValue = Json.toJson(this)

}

object ToClientEmailIsAvailableMessage {

  case object ToClientEmailIsAvailable extends SocketMessageType {
    override val description = "EMAIL_IS_AVAILABLE"
  }

  import EmailAvailability.emailAvailabilityWrites

  implicit val toClientEmailIsAvailableMessageWrites: Writes[ToClientEmailIsAvailableMessage] =
    new Writes[ToClientEmailIsAvailableMessage] {
      def writes(toClientEmailIsAvailableMessage: ToClientEmailIsAvailableMessage) = Json.obj(
        "payload" -> toClientEmailIsAvailableMessage.payload,
        "socketMessageType" -> toClientEmailIsAvailableMessage.socketMessageType
      )
    }

}