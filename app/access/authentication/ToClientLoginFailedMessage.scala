package access.authentication

import play.api.libs.json.{JsValue, Json, Writes}
import socket.SocketMessageType.ToClientLoginFailed
import socket.{SocketMessageType, ToClientSocketMessage}

case object ToClientLoginFailedMessage extends ToClientSocketMessage {

  override val socketMessageType: SocketMessageType = ToClientLoginFailed

  override val payload = ""

  implicit val toClientLoginFailedMessageWrites: Writes[ToClientSocketMessage] =
    new Writes[ToClientSocketMessage] {
      def writes(toClientLoginFailedMessage: ToClientSocketMessage) = Json.obj(
        "payload" -> "",
        "socketMessageType" -> socketMessageType
      )
  }
  override def toJson: JsValue = Json.toJson(this)

}
