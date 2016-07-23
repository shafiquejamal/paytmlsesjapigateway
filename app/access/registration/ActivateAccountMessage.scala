package access.registration

import org.apache.commons.validator.routines.EmailValidator
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{Reads, _}

case class ActivateAccountMessage(email: String, code: String) {

  private val emailValidator = EmailValidator.getInstance()

  require(email.trim.nonEmpty)
  require(code.trim.nonEmpty)
  require(emailValidator.isValid(email.trim))

}

object ActivateAccountMessage {

  implicit val ResetPasswordMessageReads: Reads[ActivateAccountMessage] = (
    (JsPath \ "email").read[String](email) and
    (JsPath \ "code").read[String](minLength[String](1))
    ) (ActivateAccountMessage.apply _)

}
