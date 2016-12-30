package access.authentication

import access.authentication.EmailMessage._
import org.scalatest.{FlatSpecLike, ShouldMatchers}
import play.api.libs.json.Json

import scala.util.{Failure, Try}

class EmailMessageUTest extends FlatSpecLike with ShouldMatchers {

  val invalidEmail = "invalid"

  "Valid email in JSON" should "be converted to an EmailMessage" in {
    val validEmail = "valid@email.com"
    val jSONMessage = Json.obj("email" -> validEmail)
    val validatedMessage = jSONMessage.validate[EmailMessage]
    validatedMessage.isSuccess shouldBe true
    validatedMessage.asOpt should contain(EmailMessage(validEmail))
  }

  "Invalid email in JSON" should "not be converted to an email message" in {
    val jSONMessageInvalidEmail = Json.obj("email" -> invalidEmail)
    val validatedMessage = jSONMessageInvalidEmail.validate[EmailMessage]
    validatedMessage.isError shouldBe true
  }

  it should "not be possible to form an email message with an invalid email" in {
    Try(EmailMessage(invalidEmail)) shouldBe a[Failure[_]]
  }

}
