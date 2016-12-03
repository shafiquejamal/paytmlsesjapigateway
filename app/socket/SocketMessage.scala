package socket

import access.authentication.{ToServerAuthenticateMessage, ToServerLogoutMessage}
import akka.actor.ActorRef
import chat.{ToServerChatMessage, ToServerRequestMessagesMessage}
import contact.{ToServerAddContactMessage, ToServerAddContactsMessage, ToServerRequestContactsMessage}
import org.joda.time.DateTime
import play.api.libs.json.{JsValue, Json, Writes}

trait SocketMessageType {

  def description: String

}

sealed trait ToServerSocketMessageType extends SocketMessageType {

  def socketMessage(msg: JsValue): ToServerSocketMessage

}

trait ToServerAddContactOrContactsMessage extends ToServerSocketMessage

object ToServerSocketMessageType {

  case object ToServerChat extends ToServerSocketMessageType {
    override val description = "toServerChat"
    override def socketMessage(msg: JsValue): ToServerChatMessage = ToServerChatMessage(
      (msg \ "recipient").validate[String].getOrElse(""),
      (msg \ "text").validate[String].getOrElse("")
    )
  }

  case object ToServerRequestMessages extends ToServerSocketMessageType {
    override val description = "toServerRequestMessages"
    override def socketMessage(msg: JsValue): ToServerRequestMessagesMessage = ToServerRequestMessagesMessage(
      (msg \ "afterDateTimeInMillis").asOpt[Long].map(millis => new DateTime(millis))
    )
  }

  case object ToServerRequestContacts extends ToServerSocketMessageType {
    override val description = "toServerRequestContacts"
    override def socketMessage(msg: JsValue): ToServerRequestContactsMessage = ToServerRequestContactsMessage(
      (msg \ "md5ofContacts").validate[String].getOrElse("")
    )
  }

  case object ToServerAddContact extends ToServerSocketMessageType {
    override val description = "toServerAddContact"
    override def socketMessage(msg: JsValue): ToServerAddContactMessage = ToServerAddContactMessage(
       (msg \ "usernameOfContactToAdd").validate[String].getOrElse("")
    )
  }

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

  case object ToServerAddContacts extends ToServerSocketMessageType {
    override val description = "toServerAddContacts"
    override def socketMessage(msg: JsValue): ToServerAddContactsMessage = ToServerAddContactsMessage(
       (msg \ "usernamesOfContactToAdd").validate[Seq[String]].getOrElse(Seq())
    )
  }

  private val socketMessageTypeFrom = Map[String, ToServerSocketMessageType](
    ToServerChat.description -> ToServerChat,
    ToServerRequestMessages.description -> ToServerRequestMessages,
    ToServerRequestContacts.description -> ToServerRequestContacts,
    ToServerAddContact.description -> ToServerAddContact,
    ToServerAddContacts.description -> ToServerAddContacts,
    ToServerAuthenticate.description -> ToServerAuthenticate,
    ToServerLogout.description -> ToServerLogout
  )

  def from(description:String): ToServerSocketMessageType = socketMessageTypeFrom(description)

}

object SocketMessageType {

  case object ToClientChat extends SocketMessageType {
    override val description = "RECEIVE_MESSAGE"
  }

  case object ToClientMessagesSince extends SocketMessageType {
    override val description = "UPDATE_MESSAGES"
  }

  case object ToClientAllContacts extends SocketMessageType {
    override val description = "UPDATE_CONTACTS"
  }

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

trait SocketMessage {

  def socketMessageType: SocketMessageType

}

trait ToClientSocketMessage extends SocketMessage {

  def payload: AnyRef

  def toJson: JsValue


}

trait ToServerSocketMessage extends SocketMessage {

  def sendTo(toServerMessageActor: ActorRef): Unit = toServerMessageActor ! this

}