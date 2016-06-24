package registration

import javax.inject.Inject

import play.api.libs.json.{Json, JsError, JsSuccess}
import play.api.mvc._
import registration.RegistrationMessage._

import scala.util.Success

class RegistrationController @Inject() (registrationAPI: RegistrationAPI) extends Controller {

  def register = Action(parse.json) { request =>
    request.body.validate[RegistrationMessage] match {
      case success:JsSuccess[RegistrationMessage] =>
        registrationAPI.signUp(success.get) match {
          case Success(_) =>
            Ok(Json.obj("status" -> "success"))
          case _ =>
            Ok(Json.obj("status" -> "failed to add user"))
        }
      case error:JsError =>
        Ok(Json.obj("status" -> "could not form registration message"))
    }
  }

}
