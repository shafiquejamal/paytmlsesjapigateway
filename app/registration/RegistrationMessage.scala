package registration

import org.apache.commons.validator.routines.EmailValidator
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

case class RegistrationMessage(maybeUsername:Option[String], email:String, password:String) {

  private val emailValidator = EmailValidator.getInstance()

  require(emailValidator.isValid(email.trim))
  require(password.trim.nonEmpty)
  require(
    maybeUsername.fold(true)(username =>
      (emailValidator.isValid(username) && username == email.trim) | !emailValidator.isValid(username)))
}

object RegistrationMessage {

  implicit val registrationMessageReads: Reads[RegistrationMessage] = (
    (JsPath \ "username").readNullable[String] and
    (JsPath \ "email").read[String](email keepAnd minLength[String](1)) and
    (JsPath \ "password").read[String](minLength[String](1))
    )(RegistrationMessage.apply _)

}


