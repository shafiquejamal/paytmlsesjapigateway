package domain.twittersearch

import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}

case class SearchTerm(userId: UUID, searchText: String, createdAt: DateTime)

object SearchTerm {

  implicit val writes: Writes[SearchTerm] = (
    (JsPath \ "searchText").write[String] and
    (JsPath \ "createdAt").write[DateTime]
  ) ( message =>  (message.searchText, message.createdAt))

}
