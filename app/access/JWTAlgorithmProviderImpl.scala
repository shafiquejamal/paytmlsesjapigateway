package access

import pdi.jwt.JwtAlgorithm
import pdi.jwt.algorithms.JwtAsymetricAlgorithm

class JWTAlgorithmProviderImpl extends JWTAlgorithmProvider {

  override def algorithm: JwtAsymetricAlgorithm = JwtAlgorithm.ES512

}
