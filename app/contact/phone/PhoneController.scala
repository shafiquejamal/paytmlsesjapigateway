package contact.phone

import access.authentication.AuthenticationAPI
import access.{AuthenticatedActionCreator, JWTParamsProvider}
import com.google.inject.Inject
import play.Configuration
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Controller, _}
import util.TimeProvider

class PhoneController @Inject() (
    override val authenticationAPI: AuthenticationAPI,
    override val jWTParamsProvider: JWTParamsProvider,
    override val timeProvider: TimeProvider,
    override val configuration: Configuration)
  extends Controller
  with AuthenticatedActionCreator {

  def registerPhoneNumber = Action(parse.json) { request =>
    request.body.validate[PhoneNumberRegistrationMessage] match {
      case success:JsSuccess[PhoneNumberRegistrationMessage] =>
        Ok(Json.obj("status" -> "success"))
      case error:JsError =>
        BadRequest
    }
  }

}