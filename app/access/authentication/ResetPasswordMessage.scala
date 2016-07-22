package access.authentication

import org.apache.commons.validator.routines.EmailValidator
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{Reads, _}

case class ResetPasswordMessage(email: String, code: String, newPassword: String) {

  private val emailValidator = EmailValidator.getInstance()

  require(email.trim.nonEmpty)
  require(code.trim.nonEmpty)
  require(newPassword.trim.nonEmpty)
  require(emailValidator.isValid(email.trim))

}

object ResetPasswordMessage {

  implicit val ResetPasswordMessageReads: Reads[ResetPasswordMessage] = (
    (JsPath \ "email").read[String](email) and
    (JsPath \ "code").read[String](minLength[String](1)) and
    (JsPath \ "newPassword").read[String](minLength[String](1))
    ) (ResetPasswordMessage.apply _)

}
