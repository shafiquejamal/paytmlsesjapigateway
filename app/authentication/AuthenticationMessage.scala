package authentication

import org.apache.commons.validator.routines.EmailValidator
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

case class AuthenticationMessage(maybeUsername:Option[String], maybeEmail:Option[String], password:String) {

  private val emailValidator = EmailValidator.getInstance()

  require( !maybeUsername.map(_.trim).forall(_.isEmpty) || !maybeEmail.forall(_.isEmpty) )
  require( maybeEmail.fold(true)(email => emailValidator.isValid(email)) )
  require( password.trim.nonEmpty )

}

object AuthenticationMessage {

  implicit val AuthenticationMessageReads: Reads[AuthenticationMessage] = (
    (JsPath \ "username").readNullable[String] and
    (JsPath \ "email").readNullable[String](email) and
    (JsPath \ "password").read[String](minLength[String](1))
    )(AuthenticationMessage.apply _)

}