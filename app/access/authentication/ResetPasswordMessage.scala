package access.authentication

import entrypoint.{SocketMessageType, ToServerSocketMessage, ToServerSocketMessageType}
import org.apache.commons.validator.routines.EmailValidator
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{Reads, _}

case class ResetPasswordMessage(email: String, code: String, newPassword: String)
  extends ToServerSocketMessage {

  override val socketMessageType: SocketMessageType = ResetPasswordMessage.ResetPassword

  private val emailValidator = EmailValidator.getInstance()

  require(email.trim.nonEmpty)
  require(code.trim.nonEmpty)
  require(newPassword.trim.nonEmpty)
  require(emailValidator.isValid(email.trim))

}

object ResetPasswordMessage {

  implicit val resetPasswordMessageReads: Reads[ResetPasswordMessage] = (
    (JsPath \ "email").read[String](email) and
    (JsPath \ "code").read[String](minLength[String](1)) and
    (JsPath \ "newPassword").read[String](minLength[String](1))
    ) (ResetPasswordMessage.apply _)

  case object ResetPassword extends ToServerSocketMessageType {
    override val description = "toServerResetPassword"
    override def socketMessage(msg: JsValue): ResetPasswordMessage =
      resetPasswordMessageReads.reads(msg).asOpt.getOrElse(ResetPasswordMessage("1", "2" , "3"))
  }

}
