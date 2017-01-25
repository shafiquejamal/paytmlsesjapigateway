package messaging

import play.api.libs.json.{JsValue, Json, Writes}

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