package user

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

case class ChangePasswordMessage(currentPassword: String, newPassword: String) {
  require(currentPassword.trim.nonEmpty)
  require(newPassword.trim.nonEmpty)
}

object ChangePasswordMessage {

  implicit val changePasswordMessageReads: Reads[ChangePasswordMessage] = (
    (JsPath \ "currentPassword").read[String] and
    (JsPath \ "newPassword").read[String]
    ) (ChangePasswordMessage.apply _)

}
