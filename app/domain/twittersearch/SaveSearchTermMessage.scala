package domain.twittersearch

import messaging.{SocketMessageType, ToServerSocketMessage, ToServerSocketMessageType}
import play.api.libs.json.Reads._
import play.api.libs.json._

import scala.util.Try

case class SaveSearchTermMessage(searchText: String) extends ToServerSocketMessage {
  require(searchText.nonEmpty)

  override val socketMessageType: SocketMessageType = SaveSearchTermMessage.SaveSearchTerm
}


object SaveSearchTermMessage {

  implicit val reads: Reads[SaveSearchTermMessage] =
    (JsPath \ "searchText").read[String].map( text => SaveSearchTermMessage(text))

  case object SaveSearchTerm extends ToServerSocketMessageType {
    override val description = "toServerSaveSearchTerm"

    override def socketMessage(msg: JsValue): SaveSearchTermMessage =
      Try(reads.reads(msg)).toOption.flatMap(_.asOpt).getOrElse(SaveSearchTermMessage(" "))
  }

}