package access.registration

import communication.{SocketMessageType, ToServerSocketMessage, ToServerSocketMessageType}
import org.apache.commons.validator.routines.EmailValidator
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{Reads, _}

import scala.util.Try

case class ActivateAccountMessage(email: String, code: String) extends ToServerSocketMessage {

  override val socketMessageType: SocketMessageType = ActivateAccountMessage.ActivateAccount

  private val emailValidator = EmailValidator.getInstance()

  require(email.trim.nonEmpty)
  require(code.trim.nonEmpty)
  require(emailValidator.isValid(email.trim))

}

object ActivateAccountMessage {

  implicit val resetPasswordMessageReads: Reads[ActivateAccountMessage] = (
    (JsPath \ "email").read[String](email) and
    (JsPath \ "code").read[String](minLength[String](1))
    ) (ActivateAccountMessage.apply _)

  case object ActivateAccount extends ToServerSocketMessageType {
    override val description = "toServerActivateAccount"
    override def socketMessage(msg: JsValue): ActivateAccountMessage =
      Try(resetPasswordMessageReads.reads(msg)).toOption.flatMap(_.asOpt).getOrElse(ActivateAccountMessage("", ""))
  }

}
