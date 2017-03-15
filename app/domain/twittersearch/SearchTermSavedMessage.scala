package domain.twittersearch

import messaging.{SocketMessageType, ToClientSocketMessage}
import play.api.libs.json.{JsValue, Json, Writes}

case class SearchTermSavedMessage(override val payload: SearchTerm) extends ToClientSocketMessage {

  override val socketMessageType: SocketMessageType = SearchTermSavedMessage.SearchTermSaved

  import SearchTermSavedMessage.writes
  override def toJson: JsValue = Json.toJson(this)
  
}

object SearchTermSavedMessage {

  case object SearchTermSaved extends SocketMessageType {
    override val description = "ADD_SEARCH_TERM"
  }

  implicit def writes: Writes[SearchTermSavedMessage] = new Writes[SearchTermSavedMessage] {
    def writes(searchTermSavedMessage: SearchTermSavedMessage) = Json.obj(
      "socketMessageType" -> searchTermSavedMessage.socketMessageType,
      "payload" -> searchTermSavedMessage.payload
    )
  }

}