package access

import pdi.jwt.algorithms.JwtAsymetricAlgorithm

trait JWTKeysProvider {

  def algorithm: JwtAsymetricAlgorithm

  def publicKey: java.security.PublicKey

  def privateKey: java.security.PrivateKey

}
