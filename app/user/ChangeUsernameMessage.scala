package user

import play.api.libs.json.Reads._
import play.api.libs.json._

case class ChangeUsernameMessage(newUsername: String) {
  require(newUsername.trim.nonEmpty)
}

object ChangeUsernameMessage {

  implicit val ChangeUsernameMessageReads: Reads[ChangeUsernameMessage] =
    (JsPath \ "newUsername").read[String].map(ChangeUsernameMessage.apply)

}