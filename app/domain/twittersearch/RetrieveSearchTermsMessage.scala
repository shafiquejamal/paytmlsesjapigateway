package domain.twittersearch

import messaging.{SocketMessageType, ToServerSocketMessage, ToServerSocketMessageType}
import play.api.libs.json.JsValue

case object RetrieveSearchTermsMessage extends ToServerSocketMessage {

  override val socketMessageType: SocketMessageType = RetrieveSearchTerms

  case object RetrieveSearchTerms extends ToServerSocketMessageType {
    override val description = "toServerRetrieveSearchTerms"

    override def socketMessage(msg: JsValue) = RetrieveSearchTermsMessage
  }


}

