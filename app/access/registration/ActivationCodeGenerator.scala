package access.registration

import java.security.MessageDigest

object ActivationCodeGenerator {

  val configurationKey = "crauth.md5ActivationKey"

  def generate(userId: String, key: String): String =
    MessageDigest.getInstance("MD5").digest((userId.toString + key).getBytes).map("%02x".format(_)).mkString
    .takeRight(9).replaceAll("0", "q").replaceAll("8", "p")

  def checkCode(userId: String, codeFromUser: String, key: String): Boolean =
    generate(userId, key: String).takeRight(9).toLowerCase == codeFromUser.replaceAll("-", "").toLowerCase

}
