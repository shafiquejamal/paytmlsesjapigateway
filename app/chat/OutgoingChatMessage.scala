package chat

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class OutgoingChatMessage(from: String, to: String, text: String, time: Long)

object OutgoingChatMessage {

  implicit val outgoingChatMessageWrites: Writes[OutgoingChatMessage] = (
    (JsPath \ "from").write[String] and
    (JsPath \ "to").write[String] and
    (JsPath \ "text").write[String] and
    (JsPath \ "time").write[Long]
  )(unlift(OutgoingChatMessage.unapply))

}