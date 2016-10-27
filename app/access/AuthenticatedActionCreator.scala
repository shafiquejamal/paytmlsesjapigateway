package access

import java.util.UUID

import access.authentication.AuthenticationAPI
import org.joda.time.DateTime
import pdi.jwt._
import pdi.jwt.algorithms.JwtHmacAlgorithm
import play.Configuration
import play.api.mvc.Results._
import play.api.mvc._
import util.TimeProvider

import scala.concurrent.Future
import scala.util.Success

trait AuthenticatedActionCreator {

  val authenticationAPI: AuthenticationAPI
  val jWTParamsProvider: JWTParamsProvider
  val secretKey: String = jWTParamsProvider.secretKey
  val algorithm: JwtHmacAlgorithm = jWTParamsProvider.algorithm
  val configuration: Configuration
  val timeProvider: TimeProvider

  def decodeAndValidateToken[A, T](
      token: String,
      block: => (UUID) => T,
      unauthorized: => T): T =
    JwtJson.decodeJson(token, secretKey, Seq(algorithm)) match {
      case Success(claim) =>
        claim.value.get("iat").flatMap(_.asOpt[DateTime]).fold[T](unauthorized) { iat =>
          val tokenExpired = iat.isBefore(timeProvider.now().minusDays(configuration.getInt("crauth.jwtValidityDays")))
          if (tokenExpired)
            unauthorized
          else {
            val userId = UUID.fromString(claim.value.get("userId").flatMap(_.asOpt[String]).getOrElse(""))
            val tokenIssuedAfterLastAllLogout = authenticationAPI.allLogoutDate(userId).fold[Boolean](true)(iat.isAfter)
            if (tokenIssuedAfterLastAllLogout)
              authenticationAPI.userById(userId).fold[T](unauthorized)(user => block(userId))
            else
              unauthorized
          }
        }
      case _ =>
        unauthorized
    }

  object AuthenticatedAction extends ActionBuilder[AuthenticatedRequest] {

    def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] =
      request.headers.get("Authorization").map(_.drop(7)).filterNot(_.trim.isEmpty)
      .fold[Future[Result]](Future.successful(Unauthorized)) { token =>
        decodeAndValidateToken(
          token,
          (userId: UUID) => block(new AuthenticatedRequest(userId, request)),
          Future.successful(Unauthorized))
      }

  }
}