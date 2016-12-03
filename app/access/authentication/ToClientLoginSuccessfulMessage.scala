package access.authentication

import play.api.libs.json.{JsValue, Json, Writes}
import socket.SocketMessageType.ToClientLoginSuccessful
import socket.{SocketMessageType, ToClientSocketMessage}

case object ToClientLoginSuccessfulMessage extends ToClientSocketMessage {

  override val socketMessageType: SocketMessageType = ToClientLoginSuccessful

  override val payload = ""

  implicit val toClientAllContactsMessageWrites: Writes[ToClientSocketMessage] =
    new Writes[ToClientSocketMessage] {
      def writes(toClientAllContactsMessage: ToClientSocketMessage) = Json.obj(
        "payload" -> "",
        "socketMessageType" -> socketMessageType
      )
  }
  override def toJson: JsValue = Json.toJson(this)

}


