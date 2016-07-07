package user

import java.util.UUID

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

case class ChangePasswordMessage(userId:UUID, currentPassword:String, newPassword:String) {
  require(currentPassword.trim.nonEmpty)
  require(newPassword.trim.nonEmpty)
}

object ChangePasswordMessage {

  implicit val changePasswordMessageReads:Reads[ChangePasswordMessage] = (
    (JsPath \ "userId").read[UUID] and
    (JsPath \ "currentPassword").read[String] and
    (JsPath \ "newPassword").read[String]
    ) (ChangePasswordMessage.apply _)

}
