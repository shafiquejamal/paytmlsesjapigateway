package access

import pdi.jwt.algorithms.JwtAsymetricAlgorithm

trait JWTAlgorithmProvider {

  def algorithm: JwtAsymetricAlgorithm

}
