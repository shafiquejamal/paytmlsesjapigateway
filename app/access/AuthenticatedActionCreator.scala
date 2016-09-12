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

  object AuthenticatedAction extends ActionBuilder[AuthenticatedRequest] {

    def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]):Future[Result] =
      request.headers.get("Authorization").fold[Future[Result]](Future.successful(Unauthorized)) { token =>
        JwtJson.decodeJson(token, secretKey, Seq(algorithm)) match {
        case Success(claim) =>
          claim.value.get("iat").flatMap(_.asOpt[DateTime]).fold[Future[Result]](Future.successful(Unauthorized)){ iat =>
            val tokenExpired = iat.isBefore(timeProvider.now().minusDays(configuration.getInt("crauth.jwtValidityDays")))
            if (tokenExpired)
              Future.successful(Unauthorized)
            else {
              val userId = UUID.fromString(claim.value.get("userId").flatMap(_.asOpt[String]).getOrElse(""))
              val tokenIssuedAfterLastAllLogout = authenticationAPI.allLogoutDate(userId).fold[Boolean](true)(iat.isAfter)
              if (tokenIssuedAfterLastAllLogout)
                authenticationAPI.userById(userId).fold[Future[Result]](Future.successful(Unauthorized))(user =>
                  block(new AuthenticatedRequest(userId, request))
                )
              else
                Future.successful(Unauthorized)
            }
          }
        case _ =>
          Future.successful(Unauthorized)
        }
      }
    }

}