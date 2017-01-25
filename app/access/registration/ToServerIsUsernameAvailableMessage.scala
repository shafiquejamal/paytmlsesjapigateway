package access.registration

import messaging.{SocketMessageType, ToServerSocketMessage, ToServerSocketMessageType}
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, JsValue, Reads}

case class ToServerIsUsernameAvailableMessage(username: String) extends ToServerSocketMessage {

  override val socketMessageType: SocketMessageType = ToServerIsUsernameAvailableMessage.ToServerIsUsernameAvailable

}

object ToServerIsUsernameAvailableMessage {

  case object ToServerIsUsernameAvailable extends ToServerSocketMessageType {
    override val description = "toServerIsUsernameAvailable"

    implicit val usernameReads: Reads[ToServerIsUsernameAvailableMessage] =
      (JsPath \ "username").read[String].map(ToServerIsUsernameAvailableMessage.apply)

    override def socketMessage(msg: JsValue): ToServerIsUsernameAvailableMessage =
      usernameReads.reads(msg).asOpt.getOrElse(ToServerIsUsernameAvailableMessage(""))
  }

}