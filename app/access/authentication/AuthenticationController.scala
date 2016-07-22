package access.authentication

import access.JWTParamsProvider
import access.authentication.AuthenticationMessage._
import access.authentication.ResetPasswordMessage._
import com.google.inject.Inject
import pdi.jwt.JwtJson
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import user.UserAPI
import user.UserStatus.{Active, Unverified}
import util.UUIDProvider

class AuthenticationController @Inject() (
    authenticationAPI: AuthenticationAPI,
    userAPI: UserAPI,
    jWTParamsProvider: JWTParamsProvider,
    uUIDProvider: UUIDProvider,
    passwordResetCodeSender: PasswordResetCodeSender)
  extends Controller {

  def authenticate = Action(parse.json) { request =>
    request.body.validate[AuthenticationMessage] match {
      case success: JsSuccess[AuthenticationMessage] =>
        authenticationAPI.user(success.get).fold(Ok(Json.obj("status" -> "authentication failed"))) { user =>
          val claim = Json.obj("userId" -> user.maybeId.getOrElse(uUIDProvider.randomUUID()).toString)
          val jWT = JwtJson.encode(claim, jWTParamsProvider.secretKey, jWTParamsProvider.algorithm)
          Ok(Json.obj("token" -> jWT, "email" -> user.email, "username" -> user.username))
        }
      case error: JsError =>
        Ok(Json.obj("status" -> "invalid data"))
    }
  }


  def sendPasswordResetLink() = Action(parse.json) { request =>
    request.body.validate[ResetPasswordMessage] match {
      case success:JsSuccess[ResetPasswordMessage] =>
        val maybeUser = userAPI.findByEmailLatest(success.get.email)
        maybeUser.fold[Unit](){ user =>
          user.userStatus match {
            case Active =>
              passwordResetCodeSender.send(user, request.host)
            case Unverified =>
            case _ =>
          }
        }
        Ok
      case error: JsError =>
       BadRequest
      }

  }
}
