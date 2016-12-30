package access

import java.security.PublicKey

trait JWTPublicKeyProvider {

  def publicKey: PublicKey

}
