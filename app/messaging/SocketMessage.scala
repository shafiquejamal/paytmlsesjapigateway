package messaging

import access.authentication.AuthenticationMessage.ToServerLogin
import access.authentication.ResetPasswordMessage.ResetPassword
import access.authentication.ToServerAuthenticateMessage.ToServerAuthenticate
import access.authentication.ToServerLogoutAllMessage.ToServerLogoutAll
import access.authentication.ToServerLogoutMessage.ToServerLogout
import access.authentication.ToServerPasswordResetRequestMessage.ToServerPasswordResetRequest
import access.registration.ActivateAccountMessage.ActivateAccount
import access.registration.RegistrationMessage.Registration
import access.registration.ToServerIsEmailAvailableMessage._
import access.registration.ToServerIsUsernameAvailableMessage.ToServerIsUsernameAvailable
import access.registration.ToServerResendActivationCodeMessage.ToServerResendActivationCode
import akka.actor.ActorRef
import play.api.libs.json.{JsValue, Json, Writes}
import user.ChangePasswordMessage.ChangePassword


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

trait ToServerSocketMessageType extends SocketMessageType {

  def socketMessage(msg: JsValue): ToServerSocketMessage

}

object ToServerSocketMessageType {

  private val socketMessageTypeFrom = Map[String, ToServerSocketMessageType](
    ResetPassword.description -> ResetPassword,
    Registration.description -> Registration,
    ToServerIsEmailAvailable.description -> ToServerIsEmailAvailable,
    ToServerIsUsernameAvailable.description -> ToServerIsUsernameAvailable,
    ActivateAccount.description -> ActivateAccount,
    ChangePassword.description -> ChangePassword,
    ToServerLogoutAll.description -> ToServerLogoutAll,
    ToServerResendActivationCode.description -> ToServerResendActivationCode,
    ToServerLogin.description -> ToServerLogin,
    ToServerPasswordResetRequest.description -> ToServerPasswordResetRequest,
    ToServerAuthenticate.description -> ToServerAuthenticate,
    ToServerLogout.description -> ToServerLogout
  )

  def from(description:String): ToServerSocketMessageType = socketMessageTypeFrom(description)

}

object SocketMessageType {

  implicit object SocketMessageTypeWrites extends Writes[SocketMessageType] {
    override def writes(socketMessageType: SocketMessageType) = Json.toJson(socketMessageType.description)
  }


}