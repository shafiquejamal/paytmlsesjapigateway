package access

import java.security.spec.{ECPoint, ECPrivateKeySpec, ECPublicKeySpec}
import java.security.{KeyFactory, PrivateKey, PublicKey, Security}

import com.google.inject.Inject
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.spec.ECNamedCurveSpec
import pdi.jwt.JwtAlgorithm
import pdi.jwt.algorithms.JwtAsymetricAlgorithm
import play.api.Configuration

class JWTParamsProviderImpl @Inject()(configuration: Configuration) extends JWTParamsProvider {

  override def algorithm: JwtAsymetricAlgorithm = JwtAlgorithm.ES512

  case class Keys(publicKey: PublicKey, privateKey: PrivateKey)

  def keys: Keys = {
    val sRaw: String = configuration.getString("crauth.S").getOrElse("")
    val xRaw: String = configuration.getString("crauth.X").getOrElse("")
    val yRaw: String = configuration.getString("crauth.Y").getOrElse("")
    val X = BigInt(xRaw, 16)
    val Y = BigInt(yRaw, 16)
    val S = BigInt(sRaw, 16)
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    val curveParams = ECNamedCurveTable.getParameterSpec("P-521")
    val curveSpec =
      new ECNamedCurveSpec("P-521", curveParams.getCurve, curveParams.getG, curveParams.getN, curveParams.getH)
    val privateSpec = new ECPrivateKeySpec(S.underlying(), curveSpec)
    val publicSpec = new ECPublicKeySpec(new ECPoint(X.underlying(), Y.underlying()), curveSpec)
    Keys(KeyFactory.getInstance("ECDSA", "BC").generatePublic(publicSpec),
      KeyFactory.getInstance("ECDSA", "BC").generatePrivate(privateSpec))
  }

  override def publicKey: PublicKey = keys.publicKey

  override def privateKey: PrivateKey = keys.privateKey
}
