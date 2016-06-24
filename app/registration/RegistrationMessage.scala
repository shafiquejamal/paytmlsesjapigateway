package registration

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class RegistrationMessage(maybeUsername:Option[String], email:String, password:String) {
  require(email.trim.nonEmpty)
  require(password.trim.nonEmpty)
}

object RegistrationMessage {

  implicit val registrationMessageReads: Reads[RegistrationMessage] = (
    (JsPath \ "username").readNullable[String] and
    (JsPath \ "email").read[String](minLength[String](1)) and
    (JsPath \ "password").read[String](minLength[String](1))
    )(RegistrationMessage.apply _)

}


