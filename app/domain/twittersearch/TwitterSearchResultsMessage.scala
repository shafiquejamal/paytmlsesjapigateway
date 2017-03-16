package domain.twittersearch

import messaging.{SocketMessageType, ToClientSocketMessage}
import org.joda.time.DateTime
import play.api.libs.json.{JsValue, Json, Writes}

case class TwitterSearchResult(id: String, user: String, createdAt: DateTime, text: String)

case object TwitterSearchResult {

  implicit def twitterSearchResultWrites: Writes[TwitterSearchResult] = new Writes[TwitterSearchResult] {
    def writes(msg: TwitterSearchResult) = Json.obj(
      "id" -> msg.id,
      "author" -> msg.user,
      "createdAt" -> msg.createdAt,
      "text" -> msg.text
    )
  }

}

case class TwitterSearchResultsMessage(override val payload: List[TwitterSearchResult]) extends ToClientSocketMessage {

  override val socketMessageType: SocketMessageType = TwitterSearchResultsMessage.TwitterSearchResults

  import TwitterSearchResultsMessage.writes
  override def toJson: JsValue = Json.toJson(this)

}

object TwitterSearchResultsMessage {

  case object TwitterSearchResults extends SocketMessageType {
    override val description = "SEARCH_RESULTS"
  }

  import TwitterSearchResult.twitterSearchResultWrites

  implicit def writes: Writes[TwitterSearchResultsMessage] = new Writes[TwitterSearchResultsMessage] {
    def writes(twitterSearchResultsMessage: TwitterSearchResultsMessage) = Json.obj(
      "socketMessageType" -> twitterSearchResultsMessage.socketMessageType,
      "payload" -> twitterSearchResultsMessage.payload
    )
  }

}