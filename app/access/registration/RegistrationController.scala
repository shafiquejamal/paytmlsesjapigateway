package access.registration

import access.JWTParamsProvider
import access.registration.RegistrationMessage._
import com.google.inject.Inject
import pdi.jwt.JwtJson
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import util.UUIDProvider

import scala.util.Success

class RegistrationController @Inject() (registrationAPI: RegistrationAPI, jWTParamsProvider: JWTParamsProvider, uUIDProvider: UUIDProvider) extends Controller {

  def register = Action(parse.json) { request =>
    request.body.validate[RegistrationMessage] match {
      case success:JsSuccess[RegistrationMessage] =>
        registrationAPI.signUp(success.get) match {
          case Success(user) =>
            val claim = Json.obj("userId" -> user.maybeId.getOrElse(uUIDProvider.randomUUID()).toString)
            val jWT = JwtJson.encode(claim, jWTParamsProvider.secretKey, jWTParamsProvider.algorithm)
            Ok(Json.obj("token" -> jWT))
          case _ =>
            Ok(Json.obj("status" -> "failed to add user"))
        }
      case error:JsError =>
        Ok(Json.obj("status" -> "could not form access.registration message"))
    }
  }

  def emailCheck(email:String) = Action {
    Ok(Json.obj("status" -> registrationAPI.isEmailIsAvailable(email)))
  }

  def usernameCheck(username:String) = Action {
    Ok(Json.obj("status" -> registrationAPI.isUsernameIsAvailable(username)))
  }

}
