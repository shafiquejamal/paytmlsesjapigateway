package access.authentication

import play.api.libs.json.{JsValue, Json, Writes}
import socket.SocketMessageType.ToClientLoggingOut
import socket.{SocketMessageType, ToClientSocketMessage}

case object ToClientLoggingOutMessage extends ToClientSocketMessage {

  override val socketMessageType: SocketMessageType = ToClientLoggingOut

  override val payload = ""

  implicit val toClientLoggingOutMessageWrites: Writes[ToClientSocketMessage] =
    new Writes[ToClientSocketMessage] {
      def writes(toClientLoggingOutMessage: ToClientSocketMessage) = Json.obj(
        "payload" -> "",
        "socketMessageType" -> socketMessageType
      )
  }
  override def toJson: JsValue = Json.toJson(this)

}
