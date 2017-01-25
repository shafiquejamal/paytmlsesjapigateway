package messaging

import access.authentication.{Authenticator, PasswordResetCodeSender}
import access.registration.AccountActivationCodeSender
import access.{JWTAlgorithmProvider, JWTPrivateKeyProvider, JWTPublicKeyProvider}
import akka.actor.{Actor, ActorLogging, _}
import com.eigenroute.id.UUIDProvider
import com.eigenroute.time.TimeProvider
import entrypoint.{AuthenticationAPI, RegistrationAPI, UserAPI, UserChecker}
import play.api.Configuration
import play.api.libs.json.JsValue

import scala.util.Try

class MessageTranslator(
    userChecker: UserChecker,
    userAPI: UserAPI,
    authenticationAPI: AuthenticationAPI,
    registrationAPI: RegistrationAPI,
    jWTAlgorithmProvider: JWTAlgorithmProvider,
    jWTPublicKeyProvider: JWTPublicKeyProvider,
    jWTPrivateKeyProvider: JWTPrivateKeyProvider,
    configuration: Configuration,
    timeProvider: TimeProvider,
    uUIDProvider: UUIDProvider,
    unnamedClient: ActorRef,
    passwordResetCodeSender: PasswordResetCodeSender,
    accountActivationLinkSender:AccountActivationCodeSender)
  extends Actor
  with ActorLogging {

  val authenticator =
    context.actorOf(
      Authenticator.props(
        userChecker,
        userAPI,
        authenticationAPI,
        registrationAPI,
        jWTAlgorithmProvider,
        jWTPublicKeyProvider,
        jWTPrivateKeyProvider,
        configuration,
        timeProvider,
        uUIDProvider,
        unnamedClient,
        passwordResetCodeSender,
        accountActivationLinkSender))

  override def receive = {

    case msg: JsValue =>
      val messageType = (msg \ "messageType").validate[String].getOrElse("")
      val maybeSocketMessage = Try(ToServerSocketMessageType.from(messageType).socketMessage(msg)).toOption
      maybeSocketMessage.foreach { socketMessage => socketMessage sendTo authenticator }

  }

}

object MessageTranslator {

  def props(
    userChecker: UserChecker,
    userAPI: UserAPI,
    authenticationAPI: AuthenticationAPI,
    registrationAPI: RegistrationAPI,
    jWTAlgorithmProvider: JWTAlgorithmProvider,
    jWTPublicKeyProvider: JWTPublicKeyProvider,
    jWTPrivateKeyProvider: JWTPrivateKeyProvider,
    configuration: Configuration,
    timeProvider: TimeProvider,
    uUIDProvider: UUIDProvider,
    unnamedClient: ActorRef,
    passwordResetCodeSender: PasswordResetCodeSender,
    accountActivationLinkSender:AccountActivationCodeSender
  ) =
    Props(
      new MessageTranslator(
        userChecker,
        userAPI,
        authenticationAPI,
        registrationAPI,
        jWTAlgorithmProvider,
        jWTPublicKeyProvider,
        jWTPrivateKeyProvider,
        configuration,
        timeProvider,
        uUIDProvider,
        unnamedClient,
        passwordResetCodeSender,
        accountActivationLinkSender))

}