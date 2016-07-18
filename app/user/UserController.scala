package user

import javax.inject.Inject

import access.authentication.AuthenticationAPI
import access.{AuthenticatedActionCreator, JWTParamsProvider}
import play.api.libs.json.{JsSuccess, Json}
import play.api.mvc.{Controller, Result}

class UserController @Inject()(
    override val authenticationAPI: AuthenticationAPI,
    override val jWTParamsProvider: JWTParamsProvider,
    userAPI:UserAPI
  )
  extends Controller
  with AuthenticatedActionCreator {

  def changePassword = AuthenticatedAction(parse.json) { request =>

    request.body.validate[ChangePasswordMessage] match {
      case successChangePasswordMessage:JsSuccess[ChangePasswordMessage] =>
        userAPI.changePassword(request.userId, successChangePasswordMessage.get)
        .toOption.fold[Result](Ok(Json.obj("status" -> "password change failed")))( user =>
          Ok(Json.obj("status" -> "success"))
        )
      case _ =>
        Ok(Json.obj("status" -> "invalid data"))
    }

  }

}
