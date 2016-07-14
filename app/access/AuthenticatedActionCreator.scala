package access

import java.util.UUID

import access.authentication.AuthenticationAPI
import com.google.inject.Inject
import pdi.jwt._
import pdi.jwt.algorithms.JwtHmacAlgorithm
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.Future
import scala.util.Success

class AuthenticatedActionCreator @Inject() (
  authenticationAPI: AuthenticationAPI,
  secretKey: String,
  algorithm: JwtHmacAlgorithm) {

  object AuthenticatedAction extends ActionBuilder[AuthenticatedRequest] {

    def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]):Future[Result] = {
      request.headers.get("token").fold[Future[Result]](Future.successful(Unauthorized)) { token =>
        JwtJson.decodeJson(token, secretKey, Seq(algorithm)) match {
        case Success(claim) =>
          val userId = UUID.fromString(claim.values.seq.headOption.map(_.toString()).getOrElse("").replace(""""""", ""))
          authenticationAPI.userById(userId).fold[Future[Result]](Future.successful(Unauthorized))(user =>
            block(new AuthenticatedRequest(userId, request))
          )
        case _ =>
          Future.successful(Unauthorized)
        }
      }
    }
  }

}