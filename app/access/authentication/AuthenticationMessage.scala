package access.authentication

import org.apache.commons.validator.routines.EmailValidator
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

case class AuthenticationMessage(maybeUsername:Option[String], maybeEmail:Option[String], password:String) {

  private val emailValidator = EmailValidator.getInstance()

  require( maybeUsername.exists(_.trim.nonEmpty) || maybeEmail.exists(_.trim.nonEmpty) )
  require( maybeEmail.filter(_.trim.nonEmpty).fold(true)(email => emailValidator.isValid(email)) )
  require( password.trim.nonEmpty )

}

object AuthenticationMessage {

  implicit val AuthenticationMessageReads: Reads[AuthenticationMessage] = (
    (JsPath \ "username").readNullable[String] and
    (JsPath \ "email").readNullable[String] and
    (JsPath \ "password").read[String](minLength[String](1))
    )(AuthenticationMessage.apply _)

}