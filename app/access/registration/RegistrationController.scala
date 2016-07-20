package access.registration

import access.JWTParamsProvider
import access.registration.RegistrationMessage._
import com.google.inject.Inject
import play.api.Configuration
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import user.{User, UserAPI}
import user.UserStatus._
import util.UUIDProvider

import scala.util.Success

class RegistrationController @Inject() (
    registrationAPI: RegistrationAPI,
    userAPI: UserAPI,
    jWTParamsProvider: JWTParamsProvider,
    uUIDProvider: UUIDProvider,
    configuration:Configuration,
    accountActivator:AccountActivator)
  extends Controller {

  val activationCodeKey = configuration.getString(ActivationCodeGenerator.configurationKey).getOrElse("")

  def register = Action(parse.json) { request =>
    request.body.validate[RegistrationMessage] match {
      case success:JsSuccess[RegistrationMessage] =>
        registerUser(request, success.get)
      case error:JsError =>
        BadRequest("could not form access.registration message")
    }
  }

  def emailCheck(email:String) = Action {
    Ok(Json.obj("status" -> registrationAPI.isEmailIsAvailable(email)))
  }

  def usernameCheck(username:String) = Action {
    Ok(Json.obj("status" -> registrationAPI.isUsernameIsAvailable(username)))
  }

  def activate(email:String, code:String) = Action {
    userAPI.findByEmailLatest(email).fold[Result](BadRequest) { user =>
      val userId = user.maybeId.map(_.toString).getOrElse("")
      if (ActivationCodeGenerator.checkCode(userId, code, activationCodeKey)) {
        activateUser(user, code)
      } else {
        BadRequest
      }
    }
  }

  private def activateUser(user:User, code:String) = {
    user.userStatus match {
      case Unverified | Deactivated =>
        registrationAPI.activate(user.maybeId.get) match {
          case Success(activatedUser) =>
            Ok(Json.obj("status" -> "success"))
          case _ =>
            BadRequest
        }
      case Blocked =>
        Ok(Json.obj("error" -> "this user is blocked"))
      case Admin | Active =>
        Ok(Json.obj("error" -> "this user is already active"))
      }
  }

  private def registerUser(request: Request[JsValue], registrationMessage:RegistrationMessage) =
    registrationAPI.signUp(registrationMessage, accountActivator.statusOnRegistration) match {
      case Success(user) =>
        accountActivator.sendActivationCode(
          user,
          configuration.getString("crauth.protocol").getOrElse("http") + "://" + request.host,
          activationCodeKey)
        Ok(Json.obj("status" -> "success"))
      case _ =>
        Ok(Json.obj("status" -> "failed to add user"))
    }
}
