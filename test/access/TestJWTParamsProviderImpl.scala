package access

import java.security.spec.{ECPoint, ECPrivateKeySpec, ECPublicKeySpec}
import java.security.{KeyFactory, PrivateKey, PublicKey, Security}

import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.spec.ECNamedCurveSpec
import pdi.jwt.JwtAlgorithm
import pdi.jwt.algorithms.JwtAsymetricAlgorithm

class TestJWTParamsProviderImpl extends JWTParamsProvider {

  override def algorithm: JwtAsymetricAlgorithm = JwtAlgorithm.ES512

  def keys: (PublicKey, PrivateKey) = {
    val sRawTest: String =
      "1a0d485996add96f832dd7f17ba7e3f056523ca3df3654c19e74bc53df7baa44e3332d660eceeaa9613dc3a93a39030ea6deda0c12d4eb0e0dcf993b9b7afb5d8b5"
    val xRawTest: String =
      "1920e638ce49a04e50e54a7c62e339e1ce3ec47f8ad5e8cdadb0ea4dbac52045b7981b1a4474ebd6c7e5c3cd2b18056d70b1b136a8d6a5f4d7f1c65cf9b8e485af8"
    val yRawTest: String =
      "183b4f85e61b271fa204968c5ce49d1b9f35e4a8b8fc37deba37c732af84ad9205fd70b57acb0e891e5ad7169967af8c88abf4fbb1cb22c4b3954a06a2842791d6"
    val X = BigInt(xRawTest, 16)
    val Y = BigInt(yRawTest, 16)
    val S = BigInt(sRawTest, 16)
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider())
    val curveParams = ECNamedCurveTable.getParameterSpec("P-521")
    val curveSpec =
      new ECNamedCurveSpec("P-521", curveParams.getCurve, curveParams.getG, curveParams.getN, curveParams.getH)
    val privateSpec = new ECPrivateKeySpec(S.underlying(), curveSpec)
    val publicSpec = new ECPublicKeySpec(new ECPoint(X.underlying(), Y.underlying()), curveSpec)
    (KeyFactory.getInstance("ECDSA", "BC").generatePublic(publicSpec),
      KeyFactory.getInstance("ECDSA", "BC").generatePrivate(privateSpec))
  }

  override def publicKey: PublicKey = keys._1

  override def privateKey: PrivateKey = keys._2

}
