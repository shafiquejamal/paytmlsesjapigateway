package access.registration

import java.security.MessageDigest

object ActivationCodeGenerator {

  val configurationKey = "crauth.md5ActivationKey"

  def generate(userId: String, key: String): String =
    MessageDigest.getInstance("MD5").digest((userId.toString + key).getBytes).map("%02x".format(_)).mkString

  def checkCode(userId: String, md5hash: String, key: String): Boolean = generate(userId, key: String) == md5hash

}
