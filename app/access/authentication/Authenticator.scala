package access.authentication

import java.util.UUID

import access._
import access.registration._
import akka.actor._
import clientmessaging.ClientPaths._
import clientmessaging.NamedClient
import com.eigenroute.id.UUIDProvider
import com.eigenroute.time.TimeProvider
import communication.{ToClientNoPayloadMessage, ToClientSocketMessage, ToServerMessageRouter, ToServerSocketMessage}
import entrypoint._
import pdi.jwt.JwtJson
import play.api.Configuration
import play.api.libs.json.Json
import user.UserStatus.{Active, Unverified}
import user.{ChangePasswordMessage, UserMessage}

import scala.util.{Failure, Success}

class Authenticator (
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

  var namedClient: ActorRef = _
  var toServerMessageRouter: ActorRef = _
  var clientUserId: UUID = _

  override def receive = {

    case authenticationRequest: ToServerAuthenticateMessage =>
      val maybeValidUser = userChecker.decodeAndValidateToken[Option[(UUID, String)]](
          authenticationRequest.jwt,
          (uUID, clientUsername) => Some(uUID, clientUsername),
          None,
          AllowedTokens(MultiUse))

      maybeValidUser.fold {
        unnamedClient ! ToClientLogoutMessage.toJson
      } { case (clientId, clientUsername) => createNamedClientAndRouter(clientId, clientUsername) }

    case authenticationMessage: AuthenticationMessage =>
      val maybeUserMessage = authenticationAPI.user(authenticationMessage)
      maybeUserMessage.foreach { case UserMessage(Some(clientId), clientUsername, _, _) =>
        createNamedClientAndRouter(clientId, clientUsername)
      }

      val response: ToClientSocketMessage =
        maybeUserMessage.fold[ToClientSocketMessage](ToClientLoginFailedMessage){
        case UserMessage(Some(clientId), clientUsername, clientEmail, _) =>
          val claim = Json.obj("userId" -> clientId.toString, "iat" -> timeProvider.now())
          val jWT = JwtJson.encode(claim, jWTPrivateKeyProvider.privateKey, jWTAlgorithmProvider.algorithm)
          ToClientLoginSuccessfulMessage(SuccessfulLoginPayload(clientUsername, clientEmail, jWT))
      }
      unnamedClient ! response.toJson

    case toServerPasswordResetRequestMessage: ToServerPasswordResetRequestMessage =>
      val maybeUser = userAPI.findByEmailLatest(toServerPasswordResetRequestMessage.email)
      maybeUser.fold[Unit](){ user =>
        user.userStatus match {
          case Active =>
            passwordResetCodeSender.send(user, "")
          case Unverified =>
          case _ =>
        }
      }
      unnamedClient ! ToClientPasswordResetCodeSentMessage.toJson

    case resetPasswordMessage: ResetPasswordMessage =>
      authenticationAPI
        .resetPassword(
            resetPasswordMessage.email,
            resetPasswordMessage.code.replaceAll("-", ""),
            resetPasswordMessage.newPassword) match {
          case Success(user) =>
            unnamedClient ! ToClientPasswordResetSuccessfulMessage.toJson
          case Failure(failure) =>
            unnamedClient ! ToClientPasswordResetFailedMessage.toJson
        }

    case isEmailAvailableMessage : ToServerIsEmailAvailableMessage =>
      val isEmailAvailable: Boolean = registrationAPI.isEmailIsAvailable(isEmailAvailableMessage.email)
      unnamedClient ! ToClientEmailIsAvailableMessage(
        EmailAvailability(isEmailAvailableMessage.email, isEmailAvailable)).toJson

    case isUsernameAvailableMessage: ToServerIsUsernameAvailableMessage =>
      val isUsernameAvailable: Boolean = registrationAPI.isUsernameIsAvailable(isUsernameAvailableMessage.username)
      unnamedClient ! ToClientUsernameIsAvailableMessage(
        UsernameAvailability(isUsernameAvailableMessage.username, isUsernameAvailable)).toJson

    case registrationMessage: RegistrationMessage =>
      val maybeUserMessage = registrationAPI.signUp(registrationMessage, accountActivationLinkSender.statusOnRegistration)
      val response =
        maybeUserMessage.toOption.fold[ToClientSocketMessage](ToClientRegistrationFailedMessage){ _ =>
          ToClientRegistrationSuccessfulMessage
        }
      unnamedClient ! response.toJson
  }

  def processAuthenticatedRequests: Receive = {

    case authenticationRequest: ToServerAuthenticateMessage =>
      unnamedClient ! ToClientAlreadyAuthenticatedMessage.toJson

    case ToServerLogoutMessage =>
      unnamedClient ! ToClientLoggingOutMessage.toJson
      context.unbecome()

    case changePasswordMessage: ChangePasswordMessage =>
      val maybeUserMessage = userAPI.changePassword(clientUserId, changePasswordMessage).toOption
      val response = maybeUserMessage.fold[ToClientNoPayloadMessage](ToClientPasswordChangeFailedMessage){ _ =>
        ToClientPasswordChangeSuccessfulMessage }
      unnamedClient ! response.toJson
      unnamedClient ! ToClientLogoutMessage.toJson
      context.unbecome()

    case msg: ToServerSocketMessage =>
      msg sendTo toServerMessageRouter

  }

  private def createNamedClientAndRouter(clientId: UUID, clientUsername: String): Unit = {
    clientUserId = clientId
    namedClient =
      context.actorOf(
        NamedClient.props(unnamedClient), namedClientActorName(clientId, uUIDProvider.randomUUID()))
    toServerMessageRouter =
      context.actorOf(
        ToServerMessageRouter.props(
          namedClient, userAPI, clientId, clientUsername, timeProvider, uUIDProvider))
    context.become(processAuthenticatedRequests)
  }

}

object Authenticator {

  def props(
      chatAuthenticator: UserChecker,
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
      new Authenticator(
        chatAuthenticator,
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
