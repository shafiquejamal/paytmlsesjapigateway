package user

import communication.{SocketMessageType, ToServerSocketMessage, ToServerSocketMessageType}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

import scala.util.Try

case class ChangePasswordMessage(currentPassword: String, newPassword: String) extends ToServerSocketMessage {
  require(currentPassword.trim.nonEmpty)
  require(newPassword.trim.nonEmpty)

  override val socketMessageType: SocketMessageType = ChangePasswordMessage.ChangePassword
}

object ChangePasswordMessage {

  implicit val changePasswordMessageReads: Reads[ChangePasswordMessage] = (
    (JsPath \ "currentPassword").read[String] and
    (JsPath \ "newPassword").read[String]
    ) (ChangePasswordMessage.apply _)

  case object ChangePassword extends ToServerSocketMessageType {
    override val description = "CHANGE_PASSWORD"

    override def socketMessage(msg: JsValue): ChangePasswordMessage =
      Try(changePasswordMessageReads.reads(msg)).toOption.flatMap(_.asOpt).getOrElse(ChangePasswordMessage(".", "."))
  }

}
