package access.registration

import org.scalatest.{ShouldMatchers, FlatSpec}
import user.TestUserImpl
import util.TestUUIDProviderImpl

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

    ActivationCodeGenerator.checkCode(user1Id, code, md5key) shouldBe true
    ActivationCodeGenerator.checkCode(user1Id, code, "an-incorrect-key") shouldBe false
    ActivationCodeGenerator.checkCode(user2Id, code, md5key) shouldBe false

  }

}
