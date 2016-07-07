package user

import java.util.UUID

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

case class ChangeUsernameMessage(userId: UUID, newUsername: String) {
  require(newUsername.trim.nonEmpty)
}

object ChangeUsernameMessage {

  implicit val ChangeUsernameMessageReads: Reads[ChangeUsernameMessage] = (
    (JsPath \ "userId").read[UUID] and
    (JsPath \ "newUsername").read[String]
    ) (ChangeUsernameMessage.apply _)

}