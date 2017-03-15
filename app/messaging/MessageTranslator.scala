package messaging

import access.authentication.{Authenticator, PasswordResetCodeSender}
import access.registration.AccountActivationCodeSender
import access.{JWTAlgorithmProvider, JWTPrivateKeyProvider, JWTPublicKeyProvider}
import akka.actor.{Actor, ActorLogging, _}
import com.eigenroute.id.UUIDProvider
import com.eigenroute.time.TimeProvider
import domain.twittersearch.API
import entrypoint.{AuthenticationAPI, RegistrationAPI, UserAPI, UserChecker}
import play.api.Configuration
import play.api.libs.json.JsValue

import scala.util.Try

class MessageTranslator(
    userChecker: UserChecker,
    userAPI: UserAPI,
    api: API,
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
        api,
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
      log.info(s"Message translator received message: $msg")
      val messageType = (msg \ "messageType").validate[String].getOrElse("")
      log.info(s"messageType: $messageType")
      val temp = ToServerSocketMessageType.from(messageType)
      log.info(s"ToServerSocketMessageType.from(messageType): $temp")
      log.info(s"socketMessage: ${temp.socketMessage(msg)}")
      val maybeSocketMessage = Try(ToServerSocketMessageType.from(messageType).socketMessage(msg)).toOption
      log.info(s"maybeSocketMessage: $maybeSocketMessage")
      maybeSocketMessage.foreach { socketMessage => socketMessage sendTo authenticator }

  }

}

object MessageTranslator {

  def props(
    userChecker: UserChecker,
    userAPI: UserAPI,
    api: API,
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
        api,
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