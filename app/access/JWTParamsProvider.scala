package access

import pdi.jwt.algorithms.JwtHmacAlgorithm

trait JWTParamsProvider {

  def secretKey: String

  def algorithm: JwtHmacAlgorithm


}
