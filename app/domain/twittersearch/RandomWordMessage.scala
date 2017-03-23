package domain.twittersearch

import messaging.{SocketMessageType, ToClientSocketMessage}
import play.api.libs.json.{Writes, Json, JsValue}

case class RandomWordMessage(override val payload: String) extends ToClientSocketMessage {

  override val socketMessageType: SocketMessageType = RandomWordMessage.RandomWord

  import RandomWordMessage.writes
  override def toJson: JsValue = Json.toJson(this)

}

object RandomWordMessage {

  case object RandomWord extends SocketMessageType {
    override val description = "RANDOM_WORD"
  }

  implicit def writes: Writes[RandomWordMessage] = new Writes[RandomWordMessage] {
    def writes(msg: RandomWordMessage) = Json.obj(
      "socketMessageType" -> msg.socketMessageType,
      "payload" -> msg.payload
    )
  }

}
