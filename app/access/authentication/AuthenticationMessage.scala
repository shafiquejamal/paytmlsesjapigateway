package access.authentication

import access.authentication.AuthenticationMessage.ToServerLogin
import communication.{SocketMessageType, ToServerSocketMessage, ToServerSocketMessageType}
import org.apache.commons.validator.routines.EmailValidator
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

import scala.util.Try

case class AuthenticationMessage(maybeUsername:Option[String], maybeEmail:Option[String], password:String)
  extends ToServerSocketMessage {

  override def socketMessageType: SocketMessageType = ToServerLogin

  private val emailValidator = EmailValidator.getInstance()

  require( maybeUsername.exists(_.trim.nonEmpty) || maybeEmail.exists(_.trim.nonEmpty) )
  require( maybeEmail.filter(_.trim.nonEmpty).fold(true)(email => emailValidator.isValid(email)) )
  require( password.trim.nonEmpty )

}

object AuthenticationMessage {

  implicit val authenticationMessageReads: Reads[AuthenticationMessage] = (
    (JsPath \ "username").readNullable[String] and
    (JsPath \ "email").readNullable[String] and
    (JsPath \ "password").read[String](minLength[String](1))
    )(AuthenticationMessage.apply _)

  case object ToServerLogin extends ToServerSocketMessageType {
    override val description = "toServerLogin"
    override def socketMessage(msg: JsValue): AuthenticationMessage =
      Try(AuthenticationMessage.authenticationMessageReads.reads(msg))
      .toOption.flatMap(_.asOpt)
      .getOrElse(AuthenticationMessage(None, None, ""))
  }

}