package access

import java.security.PublicKey
import java.util.UUID

import access.authentication.AuthenticationAPI
import com.eigenroute.time.TimeProvider
import org.joda.time.DateTime
import pdi.jwt._
import pdi.jwt.algorithms.JwtAsymetricAlgorithm
import play.api.Configuration
import play.api.libs.json.JsObject
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.Future
import scala.util.Success

trait AuthenticatedActionCreator {

  val authenticationAPI: AuthenticationAPI
  val jWTPublicKeyProvider: JWTPublicKeyProvider
  val jWTAlgorithmProvider: JWTAlgorithmProvider
  val publicKey: PublicKey = jWTPublicKeyProvider.publicKey
  val algorithm: JwtAsymetricAlgorithm = jWTAlgorithmProvider.algorithm
  val configuration: Configuration
  val timeProvider: TimeProvider

  def validateToken[T](
      block: => (UUID, String) => T,
      unauthorized: => T,
      allowedTokens: AllowedTokens,
      claim: JsObject): T =
    claim.value.get("iat").flatMap(_.asOpt[DateTime]).fold[T](unauthorized) { iat =>
      val jWTValidity = configuration.getInt("accessService.jwtValidityDays").getOrElse(-1)
      val tokenExpired = iat.isBefore(timeProvider.now().minusDays(jWTValidity)) && jWTValidity > 0
      if (tokenExpired)
        unauthorized
      else {
        val userId = UUID.fromString(claim.value.get("userId").flatMap(_.asOpt[String]).getOrElse(""))
        val tokenIssuedAfterLastAllLogout = authenticationAPI.allLogoutDate(userId).fold[Boolean](true)(iat.isAfter)
        if (tokenIssuedAfterLastAllLogout) {
          val tokenUse =
            claim.value.get("tokenUse").flatMap(_.asOpt[String]).map(TokenUse.fromDescription).getOrElse(MultiUse)
          if (allowedTokens.tokens contains tokenUse)
            tokenUse.validate(authenticationAPI, userId, iat).fold[T](unauthorized)(user => block(userId, user.username))
          else
            unauthorized
        }
        else
          unauthorized
      }
    }

  def decodeAndValidateToken[T](
      token: String,
      block: => (UUID, String) => T,
      unauthorized: => T,
      allowedTokens: AllowedTokens): T =
    JwtJson.decodeJson(token, publicKey, Seq(algorithm)) match {
      case Success(claim) =>
        validateToken(block, unauthorized, allowedTokens, claim)
      case _ =>
        unauthorized
    }

  object AuthenticatedAction extends ActionBuilder[AuthenticatedRequest] {
    def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] =
      request.headers.get("Authorization").map(_.drop(7)).filterNot(_.trim.isEmpty)
      .fold[Future[Result]](Future.successful(Unauthorized)) { token =>
        decodeAndValidateToken(
          token,
          (userId: UUID, username: String) => block(new AuthenticatedRequest(userId, username, request)),
          Future.successful(Unauthorized),
          AllowedTokens(MultiUse))
      }
  }

}