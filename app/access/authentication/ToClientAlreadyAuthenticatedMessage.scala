package access.authentication

import play.api.libs.json.{JsValue, Json, Writes}
import socket.SocketMessageType._
import socket.{SocketMessageType, ToClientSocketMessage}

case object ToClientAlreadyAuthenticatedMessage extends ToClientSocketMessage {

  override val socketMessageType: SocketMessageType = ToClientAlreadyAuthenticated

  override val payload = ""

  implicit val toClientAlreadyAuthenticatedMessageWrites: Writes[ToClientSocketMessage] =
    new Writes[ToClientSocketMessage] {
      def writes(toClientAlreadyAuthenticatedMessageWrites: ToClientSocketMessage) = Json.obj(
        "payload" -> "",
        "socketMessageType" -> socketMessageType
      )
  }
  override def toJson: JsValue = Json.toJson(this)

}
