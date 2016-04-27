package user

import com.google.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

class UserController @Inject() (userDAO:UserDAO, userAPI:UserAPI) extends Controller {

  implicit val userMessageWrites:Writes[UserMessage] = (
    (JsPath \ "id").write[String] and
    (JsPath \ "username").writeNullable[String] and
    (JsPath \ "email").writeNullable[String]
    )(unlift(UserMessage.unapply))

  def user = Action {
    Ok(userAPI.find("someId").map(Json.toJson(_)).getOrElse(""))
  }


}
