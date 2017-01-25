package access.authentication

import messaging.{SocketMessageType, ToClientSocketMessage}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsValue, Json, Writes}

case class SuccessfulLoginPayload(username: String, email: String, token: String)

object SuccessfulLoginPayload {

  implicit val successfulLoginPayloadWrites: Writes[SuccessfulLoginPayload] = (
    (JsPath \ "username").write[String] and
    (JsPath \ "email").write[String] and
    (JsPath \ "token").write[String]
    ) (message => (message.username, message.email, message.token))

}

case class ToClientLoginSuccessfulMessage(override val payload: SuccessfulLoginPayload) extends ToClientSocketMessage {

  override val socketMessageType: SocketMessageType = ToClientLoginSuccessful

  case object ToClientLoginSuccessful extends SocketMessageType {
    override val description = "LOGIN_SUCCESSFUL"
  }

  import ToClientLoginSuccessfulMessage.toClientLoginSuccessfulMessageWrites

  override def toJson: JsValue = Json.toJson(this)

}

object ToClientLoginSuccessfulMessage {

  import SuccessfulLoginPayload.successfulLoginPayloadWrites

  implicit val toClientLoginSuccessfulMessageWrites: Writes[ToClientLoginSuccessfulMessage] = (
    (JsPath \ "socketMessageType").write[SocketMessageType] and
    (JsPath \ "payload").write[SuccessfulLoginPayload]
    ) (message => (message.socketMessageType, message.payload))

}