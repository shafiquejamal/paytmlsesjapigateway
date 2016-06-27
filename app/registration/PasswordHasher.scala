package registration

import org.mindrot.jbcrypt.BCrypt

object PasswordHasher {

  def hash(plainTextPassword:String):String = BCrypt.hashpw(plainTextPassword, BCrypt.gensalt())

}
