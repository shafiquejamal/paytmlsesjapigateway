package entrypoint

import access.authentication.{ToServerAuthenticateMessage, ToServerLogoutMessage}
import akka.actor.ActorRef
import play.api.libs.json.{JsValue, Json, Writes}


trait SocketMessage {

  def socketMessageType: SocketMessageType

}

trait SocketMessageType {

  def description: String

}

trait ToClientSocketMessage extends SocketMessage {

  def payload: AnyRef

  def toJson: JsValue


}

trait ToServerSocketMessage extends SocketMessage {

  def sendTo(toServerMessageActor: ActorRef): Unit = toServerMessageActor ! this

}

sealed trait ToServerSocketMessageType extends SocketMessageType {

  def socketMessage(msg: JsValue): ToServerSocketMessage

}

object ToServerSocketMessageType {

  case object ToServerAuthenticate extends ToServerSocketMessageType {
    override val description = "toServerAuthenticate"
    override def socketMessage(msg: JsValue): ToServerAuthenticateMessage = ToServerAuthenticateMessage(
       (msg \ "jwt").validate[String].getOrElse("")
    )
  }

  case object ToServerLogout extends ToServerSocketMessageType {
    override val description = "toServerLogout"
    override def socketMessage(msg: JsValue) = ToServerLogoutMessage
  }


  private val socketMessageTypeFrom = Map[String, ToServerSocketMessageType](
    ToServerAuthenticate.description -> ToServerAuthenticate,
    ToServerLogout.description -> ToServerLogout
  )

  def from(description:String): ToServerSocketMessageType = socketMessageTypeFrom(description)

}

object SocketMessageType {

  case object ToClientLoginSuccessful extends SocketMessageType {
    override val description = "SOCKET_LOGIN_SUCCESSFUL"
  }

  case object ToClientLoginFailed extends SocketMessageType {
    override val description = "SOCKET_LOGIN_FAILED"
  }

  case object ToClientAlreadyAuthenticated extends SocketMessageType {
    override val description = "SOCKET_ALREADY_AUTHENTICATED"
  }

  case object ToClientLoggingOut extends SocketMessageType {
    override val description = "SOCKET_LOGGING_OUT"
  }

  implicit object SocketMessageTypeWrites extends Writes[SocketMessageType] {
    override def writes(socketMessageType: SocketMessageType) = Json.toJson(socketMessageType.description)
  }


}