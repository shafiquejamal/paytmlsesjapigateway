package domain.twittersearch

import messaging.{SocketMessageType, ToServerSocketMessage, ToServerSocketMessageType}
import play.api.libs.json.JsValue

case object FetchRandomWordMessage extends ToServerSocketMessage {

  override val socketMessageType: SocketMessageType = FetchRandomWord

  case object FetchRandomWord extends ToServerSocketMessageType {
    override val description = "toServerFetchRandomWord"

    override def socketMessage(msg: JsValue) = FetchRandomWordMessage
  }

}
