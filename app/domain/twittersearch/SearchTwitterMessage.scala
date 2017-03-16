package domain.twittersearch

import messaging.{ToServerSocketMessageType, SocketMessageType, ToServerSocketMessage}
import play.api.libs.json.{JsValue, JsPath, Reads}

import scala.util.Try

case class SearchTwitterMessage(searchText: String) extends ToServerSocketMessage {
  require(searchText.nonEmpty)

  override val socketMessageType: SocketMessageType = SearchTwitterMessage.SearchTwitter

}

object SearchTwitterMessage {

   implicit val reads: Reads[SearchTwitterMessage] =
    (JsPath \ "searchText").read[String].map( text => SearchTwitterMessage(text))

  case object SearchTwitter extends ToServerSocketMessageType {
    override val description = "toServerSearchTwitter"

     override def socketMessage(msg: JsValue): SearchTwitterMessage =
      Try(reads.reads(msg)).toOption.flatMap(_.asOpt).getOrElse(SearchTwitterMessage(" "))
  }

}