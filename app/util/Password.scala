package util

import org.mindrot.jbcrypt.BCrypt
import user.User

object Password {

  def hash(plainTextPassword:String):String = BCrypt.hashpw(plainTextPassword, BCrypt.gensalt())

  def passwordCheck(password:String)(user:User) = BCrypt.checkpw(password, user.hashedPassword)

}
