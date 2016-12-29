package access

import pdi.jwt.algorithms.JwtAsymetricAlgorithm

trait JWTParamsProvider {

  def algorithm: JwtAsymetricAlgorithm

  def publicKey: java.security.PublicKey

  def privateKey: java.security.PrivateKey

}
