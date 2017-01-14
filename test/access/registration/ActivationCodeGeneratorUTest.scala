package access.registration

import com.eigenroute.id.TestUUIDProviderImpl
import org.scalatest.{FlatSpec, ShouldMatchers}
import user.TestUserImpl

class ActivationCodeGeneratorUTest extends FlatSpec with ShouldMatchers {

  val uUIDProvider = new TestUUIDProviderImpl()
  uUIDProvider.index = 101
  val uUID = uUIDProvider.randomUUID()

  val user1 = new TestUserImpl()
  val user2 = new TestUserImpl().copy(maybeId = Some(uUID))
  val md5key = "some-key"

  "The code generator" should "generate a code that can later be verified to match if the key and user match" in {

    val user1Id = user1.maybeId.map(_.toString).getOrElse("")
    val user2Id = user2.maybeId.map(_.toString).getOrElse("")
    val code = ActivationCodeGenerator.generate(user1Id, md5key).takeRight(9)

    ActivationCodeGenerator.checkCode(user1Id, code.toUpperCase, md5key) shouldBe true
    ActivationCodeGenerator.checkCode(user1Id, code.toLowerCase, md5key) shouldBe true
    ActivationCodeGenerator.checkCode(user1Id, code, "an-incorrect-key") shouldBe false
    ActivationCodeGenerator.checkCode(user2Id, code, md5key) shouldBe false

  }

  "Adding dashes every x number of characters" should "add the dashes appropriately" in {

    val someCodeWithoutDashes = "abcdefghijk"
    val anotherCodeWithoutDashes = "cdefghijkl"

    ActivationCodeGenerator.codeWithDashes(someCodeWithoutDashes) shouldEqual "abc-def-ghi-jk"
    ActivationCodeGenerator.codeWithDashes(anotherCodeWithoutDashes, ".", 2) shouldEqual "cd.ef.gh.ij.kl"

  }

}
