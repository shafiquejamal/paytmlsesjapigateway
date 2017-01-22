package clientmessaging

import access.authentication.Authenticator
import access.{JWTAlgorithmProvider, JWTPublicKeyProvider}
import akka.actor.{Actor, ActorLogging, _}
import com.eigenroute.id.UUIDProvider
import com.eigenroute.time.TimeProvider
import communication.ToServerSocketMessageType
import entrypoint.{AuthenticationAPI, UserAPI, UserChecker}
import play.api.Configuration
import play.api.libs.json.JsValue

class MessageTranslator(
    userChecker: UserChecker,
    userAPI: UserAPI,
    authenticationAPI: AuthenticationAPI,
    jWTAlgorithmProvider: JWTAlgorithmProvider,
    jWTPublicKeyProvider: JWTPublicKeyProvider,
    configuration: Configuration,
    timeProvider: TimeProvider,
    uUIDProvider: UUIDProvider,
    unnamedClient: ActorRef)
  extends Actor
  with ActorLogging {

  val authenticator =
    context.actorOf(
      Authenticator.props(
        userChecker,
        userAPI,
        authenticationAPI,
        jWTAlgorithmProvider,
        jWTPublicKeyProvider,
        configuration,
        timeProvider,
        uUIDProvider,
        unnamedClient))

  override def receive = {

    case msg: JsValue =>
      val messageType = (msg \ "messageType").validate[String].getOrElse("")
      val socketMessage = ToServerSocketMessageType.from(messageType).socketMessage(msg)
      socketMessage sendTo authenticator

  }

}

object MessageTranslator {

  def props(
    userChecker: UserChecker,
    userAPI: UserAPI,
    authenticationAPI: AuthenticationAPI,
    jWTAlgorithmProvider: JWTAlgorithmProvider,
    jWTPublicKeyProvider: JWTPublicKeyProvider,
    configuration: Configuration,
    timeProvider: TimeProvider,
    uUIDProvider: UUIDProvider,
    unnamedClient: ActorRef
  ) =
    Props(
      new MessageTranslator(
        userChecker,
        userAPI,
        authenticationAPI,
        jWTAlgorithmProvider,
        jWTPublicKeyProvider,
        configuration,
        timeProvider,
        uUIDProvider,
        unnamedClient))

}