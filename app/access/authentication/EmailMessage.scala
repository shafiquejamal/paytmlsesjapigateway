package access.authentication

import org.apache.commons.validator.routines.EmailValidator
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads}

case class EmailMessage(email: String) {

  private val emailValidator = EmailValidator.getInstance()

  require(email.trim.nonEmpty)
  require(emailValidator.isValid(email.trim))

}

object EmailMessage {

  implicit val EmailMessageReads: Reads[EmailMessage] =
    (JsPath \ "email").read[String](email).map(EmailMessage.apply)

}