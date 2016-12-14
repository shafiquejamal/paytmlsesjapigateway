package contact.phone

import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads}
import scala.language.implicitConversions

case class PhoneNumberRegistrationMessage(phoneNumberToAdd: String) {
  require(phoneNumberToAdd.length == 11)
  require(phoneNumberToAdd.take(1) == "1")
}

object PhoneNumberRegistrationMessage {

  implicit val phoneNumberRegistrationMessageReads: Reads[PhoneNumberRegistrationMessage] =
    (JsPath \ "phoneNumberToAdd").read[String](minLength[String](1)).map(PhoneNumberRegistrationMessage.apply)

}
