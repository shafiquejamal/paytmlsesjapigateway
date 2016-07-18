package access.authentication

import access.JWTParamsProvider
import access.authentication.AuthenticationMessage._
import com.google.inject.Inject
import pdi.jwt.JwtJson
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import util.UUIDProvider

class AuthenticationController @Inject() (
    authenticationAPI: AuthenticationAPI,
    jWTParamsProvider: JWTParamsProvider,
    uUIDProvider: UUIDProvider)
  extends Controller {

  def authenticate = Action(parse.json) { request =>
    request.body.validate[AuthenticationMessage] match {
      case success: JsSuccess[AuthenticationMessage] =>
        authenticationAPI.user(success.get).fold(Ok(Json.obj("status" -> "authentication failed"))) { user =>
          val claim = Json.obj("userId" -> user.maybeId.getOrElse(uUIDProvider.randomUUID()).toString)
          val jWT = JwtJson.encode(claim, jWTParamsProvider.secretKey, jWTParamsProvider.algorithm)
          Ok(Json.obj("token" -> jWT))
        }
      case error: JsError =>
        Ok(Json.obj("status" -> "invalid data"))
    }
  }
}
