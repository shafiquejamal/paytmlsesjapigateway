package socket

import play.api.libs.json.{JsValue, Json, Writes}
import socket.SocketMessageType._

trait ToClientNoPayloadMessage extends ToClientSocketMessage {

  override val payload = ""

  implicit val messageWrites: Writes[ToClientSocketMessage] =
    new Writes[ToClientSocketMessage] {
      def writes(message: ToClientSocketMessage) = Json.obj(
        "payload" -> "",
        "socketMessageType" -> socketMessageType
      )
  }
  override def toJson: JsValue = Json.toJson(this)

}