package access

import java.security.spec.ECPrivateKeySpec
import java.security.{KeyFactory, PrivateKey, Security}

import com.google.inject.Inject
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.spec.ECNamedCurveSpec
import play.api.Configuration

class JWTPrivateKeyProviderImpl @Inject()(configuration: Configuration) extends JWTPrivateKeyProvider {

  override def privateKey: PrivateKey = {
    val sRaw: String = configuration.getString("crauth.S").getOrElse("")
    val S = BigInt(sRaw, 16)
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider())
    val curveParams = ECNamedCurveTable.getParameterSpec("P-521")
    val curveSpec =
      new ECNamedCurveSpec("P-521", curveParams.getCurve, curveParams.getG, curveParams.getN, curveParams.getH)
    val privateSpec = new ECPrivateKeySpec(S.underlying(), curveSpec)
    KeyFactory.getInstance("ECDSA", "BC").generatePrivate(privateSpec)
  }

}
