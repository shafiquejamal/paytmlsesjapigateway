package contact.phone

import org.scalatest.{FlatSpecLike, ShouldMatchers}
import PhoneNumberRegistrationMessage._
import play.api.libs.json.Json

import scala.util.{Failure, Try}

class PhoneNumberRegistrationMessageUTest extends FlatSpecLike with ShouldMatchers {

  val invalidPhoneNumber = "8883216111"

  "Converting a valid phone number from JSON to a message" should "succeed" in {
    val validPhoneNumber = "18883216111"
    val jSONMessage = Json.obj("phoneNumberToAdd" -> validPhoneNumber)
    val validatedMessage = jSONMessage.validate[PhoneNumberRegistrationMessage]
    validatedMessage.asOpt should contain(PhoneNumberRegistrationMessage(validPhoneNumber))
  }

  "Converting an invalid phone number from JSON to a message" should "fail" in {
    val jSONMessage = Json.obj("phoneNumberToAdd" -> invalidPhoneNumber)
    Try(jSONMessage.validate[PhoneNumberRegistrationMessage]) shouldBe a[Failure[_]]
  }

  "Forming a phone number message with an invalid phone number" should "not be possible" in {
    Try(PhoneNumberRegistrationMessage(invalidPhoneNumber)) shouldBe a[Failure[_]]
  }

}
