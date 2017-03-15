package entrypoint

import access.authentication.PasswordResetCodeSender
import access.registration.AccountActivationCodeSender
import access.{AuthenticatedActionCreator, JWTAlgorithmProvider, JWTPrivateKeyProvider, JWTPublicKeyProvider}
import akka.actor.ActorSystem
import akka.stream.Materializer
import com.eigenroute.id.UUIDProvider
import com.eigenroute.time.TimeProvider
import com.google.inject.Inject
import com.typesafe.scalalogging.LazyLogging
import messaging.MessageTranslator
import play.api.Configuration
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc._

class ClientMessagingController @Inject() (
    override val timeProvider: TimeProvider,
    override val configuration: Configuration,
    override val authenticationAPI: AuthenticationAPI,
    registrationAPI: RegistrationAPI,
    override val jWTAlgorithmProvider: JWTAlgorithmProvider,
    override val jWTPublicKeyProvider: JWTPublicKeyProvider,
    jWTPrivateKeyProvider: JWTPrivateKeyProvider,
    system: ActorSystem,
    materializer: Materializer,
    userChecker: UserChecker,
    userAPI: UserAPI,
    uUIDProvider: UUIDProvider,
    passwordResetCodeSender: PasswordResetCodeSender,
    accountActivationLinkSender:AccountActivationCodeSender)
 extends Controller
 with AuthenticatedActionCreator
 with LazyLogging {

  implicit val mat = materializer
  implicit val actorRefFactory = system

  def connect = {
    
    WebSocket.accept[JsValue, JsValue] { request =>
      ActorFlow.actorRef(unnamedClient =>
        MessageTranslator.props(
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
  }

}
