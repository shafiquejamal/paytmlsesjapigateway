package access.registration

import messaging.{SocketMessageType, ToClientSocketMessage}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsValue, Json, Writes}

case class UsernameAvailability(username: String, available: Boolean)

object UsernameAvailability {

   implicit val usernameAvailabilityWrites: Writes[UsernameAvailability] = (
    (JsPath \ "username").write[String] and
    (JsPath \ "available").write[Boolean]
    ) ( message => (message.username, message.available))

}

case class ToClientUsernameIsAvailableMessage(override val payload: UsernameAvailability) extends ToClientSocketMessage {

  override val socketMessageType: SocketMessageType = ToClientUsernameIsAvailableMessage.ToClientUsernameIsAvailable

  import ToClientUsernameIsAvailableMessage.toClientUsernameIsAvailableMessageWrites
  override def toJson: JsValue = Json.toJson(this)

}

object ToClientUsernameIsAvailableMessage {

  case object ToClientUsernameIsAvailable extends SocketMessageType {
    override val description = "USERNAME_IS_AVAILABLE"
  }

  import UsernameAvailability.usernameAvailabilityWrites

  implicit val toClientUsernameIsAvailableMessageWrites: Writes[ToClientUsernameIsAvailableMessage] =
    new Writes[ToClientUsernameIsAvailableMessage] {
      def writes(toClientUsernameIsAvailableMessage: ToClientUsernameIsAvailableMessage) = Json.obj(
        "payload" -> toClientUsernameIsAvailableMessage.payload,
        "socketMessageType" -> toClientUsernameIsAvailableMessage.socketMessageType
      )
    }

}