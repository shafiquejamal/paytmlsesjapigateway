package access.registration

import java.security.MessageDigest

object ActivationCodeGenerator {

  val configurationKey = "crauth.md5ActivationKey"

  def generate(nonHashedCode: String, key: String): String = {
    val origCode =
      MessageDigest.getInstance("MD5").digest((nonHashedCode.toString + key).getBytes).map("%02x".format(_)).mkString
    val shortenedPrettyCode =
      origCode.takeRight(9).replaceAll("0", "q").replaceAll("8", "p").replaceAll("l", "z").replaceAll("1", "2")
    shortenedPrettyCode
  }

  def checkCode(nonHashedCode: String, codeFromUser: String, key: String): Boolean =
    generate(nonHashedCode, key: String).takeRight(9).toLowerCase == codeFromUser.replaceAll("-", "").toLowerCase

}
