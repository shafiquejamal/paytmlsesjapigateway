package access.authentication

import org.scalatest.TryValues._
import org.scalatest.{FlatSpec, ShouldMatchers}

import scala.util.{Success, Try}

class AuthenticationMessageUTest extends FlatSpec with ShouldMatchers {

  "The access.authentication message" should "not be formed if both the username and the email address are empty" in {
    Try (AuthenticationMessage(Some(" "), Some("  "), "password")).failure.exception shouldBe a[RuntimeException]
  }

  it should "not be formed if the email given is not a valid email address" in {
    Try (AuthenticationMessage(Some("username"), Some("notAnEmailAddress.com"), "password")).failure.exception shouldBe
      a[RuntimeException]
  }

  it should "not be formed if the password is empty" in {
    Try (AuthenticationMessage(Some("username"), Some("new@user.com"), "   ")).failure.exception shouldBe a[RuntimeException]
  }

  it should "be formed if none of the above are true" in {
    Try (AuthenticationMessage(Some("username"), Some("new@user.com"), "password")) shouldBe a[Success[_]]
  }

}
