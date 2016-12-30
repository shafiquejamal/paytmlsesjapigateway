package access

import java.security.PrivateKey

trait JWTPrivateKeyProvider {

  def privateKey: PrivateKey

}
