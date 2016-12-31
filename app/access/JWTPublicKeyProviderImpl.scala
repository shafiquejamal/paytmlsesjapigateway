package access

import java.security.spec.{ECPoint, ECPublicKeySpec}
import java.security.{KeyFactory, PublicKey, Security}

import com.google.inject.Inject
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.spec.ECNamedCurveSpec
import play.api.Configuration

class JWTPublicKeyProviderImpl @Inject()(configuration: Configuration) extends JWTPublicKeyProvider {

  override def publicKey: PublicKey = {
    val xRaw: String = configuration.getString("accessService.X").getOrElse("")
    val yRaw: String = configuration.getString("accessService.Y").getOrElse("")
    val X = BigInt(xRaw, 16)
    val Y = BigInt(yRaw, 16)
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider())
    val curveParams = ECNamedCurveTable.getParameterSpec("P-521")
    val curveSpec =
      new ECNamedCurveSpec("P-521", curveParams.getCurve, curveParams.getG, curveParams.getN, curveParams.getH)
    val publicSpec = new ECPublicKeySpec(new ECPoint(X.underlying(), Y.underlying()), curveSpec)
    KeyFactory.getInstance("ECDSA", "BC").generatePublic(publicSpec)
  }

}
