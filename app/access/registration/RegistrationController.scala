package access.registration

import access.authentication.EmailMessage
import access.registration.RegistrationMessage._
import com.google.inject.Inject
import play.api.Configuration
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import user.UserStatus._
import user.{UserAPI, UserMessage}
import util.UUIDProvider

import scala.util.Success

class RegistrationController @Inject() (
    registrationAPI: RegistrationAPI,
    userAPI: UserAPI,
    uUIDProvider: UUIDProvider,
    configuration:Configuration,
    accountActivationLinkSender:AccountActivationCodeSender)
  extends Controller {

  val activationCodeKey = configuration.getString(ActivationCodeGenerator.configurationKey).getOrElse("")

  def register = Action(parse.json) { request =>
    request.body.validate[RegistrationMessage] match {
      case success:JsSuccess[RegistrationMessage] =>
        registerUser(request, success.get)
      case error:JsError =>
        BadRequest
    }
  }

  def emailCheck(email:String) = Action {
    Ok(Json.obj("status" -> registrationAPI.isEmailIsAvailable(email)))
  }

  def usernameCheck(username:String) = Action {
    Ok(Json.obj("status" -> registrationAPI.isUsernameIsAvailable(username)))
  }

  def activate() = Action(parse.json) { request =>
    request.body.validate[ActivateAccountMessage] match {
      case success:JsSuccess[ActivateAccountMessage] =>
        val (email, code) = (success.get.email, success.get.code)
        userAPI.findByEmailLatest(email).fold[Result](BadRequest) { user =>
          val userId = user.maybeId.map(_.toString).getOrElse("")
          if (ActivationCodeGenerator.checkCode(userId, code, activationCodeKey)) {
            activateUser(user, code)
          } else {
            Ok(Json.obj("error" -> "incorrect code"))
          }
      }
      case error:JsError =>
        BadRequest
    }

  }

  def resendActivationLink() = Action(parse.json) { request =>
    request.body.validate[EmailMessage] match {
      case success:JsSuccess[EmailMessage] =>
        userAPI
        .findUnverifiedUser(success.get.email)
        .foreach( user => accountActivationLinkSender.sendActivationCode(user, activationCodeKey))
        Ok
      case error:JsError =>
        BadRequest
    }
  }

  private def activateUser(user:UserMessage, code:String) = {
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
    registrationAPI.signUp(registrationMessage, accountActivationLinkSender.statusOnRegistration) match {
      case Success(user) =>
        accountActivationLinkSender
        .sendActivationCode(user, activationCodeKey)
        Ok(Json.obj("status" -> "success"))
      case _ =>
        Ok(Json.obj("status" -> "failed to add user"))
    }
}
