package access.registration

import org.scalatest.TryValues._
import org.scalatest.{FlatSpec, ShouldMatchers}

import scala.util.{Success, Try}

class RegistrationMessageUTest extends FlatSpec with ShouldMatchers {

  "A access.registration message" should "not be formed if the username is an email address that is not the same as the email" +
  "address in the message" in {
    Try (RegistrationMessage(Some("new@user.com"), "old@user.com", "pass")).failure.exception shouldBe a[RuntimeException]
  }

  it should "not be formed if the email address is not a valid email address" in {
    Try (RegistrationMessage(Some("newuser"), "olduser.com", "pass")).failure.exception shouldBe a[RuntimeException]
  }

  it should "not be formed if the password is empty or only white space" in {
    Try (RegistrationMessage(Some("newuser"), "new@user.com", " ")).failure.exception shouldBe a[RuntimeException]
  }

  it should "be formed if the none of the above conditions hold" in {
    Try (RegistrationMessage(Some("newuser"), "new@user.com", "password")) shouldBe a[Success[_]]
  }

}
