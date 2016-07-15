package access

import pdi.jwt.JwtAlgorithm
import pdi.jwt.algorithms.JwtHmacAlgorithm

class TestJWTParamsProviderImpl extends JWTParamsProvider {

  override def secretKey = "some secret key"

  override def algorithm: JwtHmacAlgorithm = JwtAlgorithm.HS256

}
