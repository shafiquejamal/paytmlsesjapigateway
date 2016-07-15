package access

import com.google.inject.Inject
import pdi.jwt.JwtAlgorithm
import pdi.jwt.algorithms.JwtHmacAlgorithm
import play.api.Configuration

class JWTParamsProviderImpl @Inject() (configuration: Configuration) extends JWTParamsProvider {

  def secretKey: String = configuration.getString("crauth.jWTSecretKey").getOrElse("")

  def algorithm: JwtHmacAlgorithm = JwtAlgorithm.HS256

}
