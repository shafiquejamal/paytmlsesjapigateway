package domain.twittersearch

import messaging.{SocketMessageType, ToClientSocketMessage}
import play.api.libs.json.{JsValue, Json, Writes}

case class TwitterSearchResultsMessage(override val payload: List[String]) extends ToClientSocketMessage {

  override val socketMessageType: SocketMessageType = TwitterSearchResultsMessage.TwitterSearchResults

  import TwitterSearchResultsMessage.writes
  override def toJson: JsValue = Json.toJson(this)

}

object TwitterSearchResultsMessage {

  case object TwitterSearchResults extends SocketMessageType {
    override val description = "SEARCH_RESULTS"
  }

  implicit def writes: Writes[TwitterSearchResultsMessage] = new Writes[TwitterSearchResultsMessage] {
    def writes(twitterSearchResultsMessage: TwitterSearchResultsMessage) = Json.obj(
      "socketMessageType" -> twitterSearchResultsMessage.socketMessageType,
      "payload" -> twitterSearchResultsMessage.payload
    )
  }

}