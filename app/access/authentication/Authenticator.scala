package access.authentication

import java.util.UUID

import access._
import access.registration._
import akka.actor._
import com.eigenroute.id.UUIDProvider
import com.eigenroute.time.TimeProvider
import entrypoint._
import messaging.ClientPaths._
import messaging._
import pdi.jwt.JwtJson
import play.api.Configuration
import play.api.libs.json.Json
import user.UserStatus._
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

  val activationCodeKey = configuration.getString(ActivationCodeGenerator.configurationKey).getOrElse("")
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
      log.info(s"authenticator received: $registrationMessage")
      val maybeUserMessage = registrationAPI.signUp(registrationMessage, accountActivationLinkSender.statusOnRegistration)
      val response =
        maybeUserMessage.toOption.fold[ToClientSocketMessage](ToClientRegistrationFailedMessage){ userMessage =>
          accountActivationLinkSender
          .sendActivationCode(userMessage, activationCodeKey)
          ToClientRegistrationSuccessfulMessage
        }
      unnamedClient ! response.toJson

    case activateAccountMessage: ActivateAccountMessage =>
      val (email, code) = (activateAccountMessage.email, activateAccountMessage.code)
      val response: ToClientSocketMessage = userAPI.findByEmailLatest(email).fold[ToClientSocketMessage]{
          ToClientAccountActivationFailedMessage("User does not exist")
        } { user =>
          val userId = user.maybeId.map(_.toString).getOrElse("")
          if (ActivationCodeGenerator.checkCode(userId, code, activationCodeKey)) {
            activateUser(user, code)
          } else {
            ToClientAccountActivationFailedMessage("Incorrect code")
          }
        }
      unnamedClient ! response.toJson

    case toServerResendActivationCodeMessage: ToServerResendActivationCodeMessage =>
      val response = userAPI.findUnverifiedUser(toServerResendActivationCodeMessage.email).fold[ToClientSocketMessage] {
        ToClientResendActivationCodeResultMessage("User not registered or already verified")
      } { user =>
        accountActivationLinkSender.sendActivationCode(user, activationCodeKey)
        ToClientResendActivationCodeResultMessage("Code sent")
      }
      unnamedClient ! response.toJson

  }

  def processAuthenticatedRequests: Receive = {

    case authenticationRequest: ToServerAuthenticateMessage =>
      unnamedClient ! ToClientAlreadyAuthenticatedMessage.toJson

    case ToServerLogoutMessage =>
      unnamedClient ! ToClientLoggingOutMessage.toJson
      context.unbecome()

    case ToServerLogoutAllMessage =>
      authenticationAPI.logoutAllDevices(clientUserId)
      val allAuthenticatorsForThisUser = context.actorSelection(namedClientPath(clientUserId))
      allAuthenticatorsForThisUser ! ToServerLogoutMessage

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

  private def activateUser(user:UserMessage, code:String): ToClientSocketMessage = {
    user.userStatus match {
      case Unverified | Deactivated =>
        registrationAPI.activate(user.maybeId.get) match {
          case Success(activatedUser) =>
            ToClientAccountActivationSuccessfulMessage
          case _ =>
            ToClientAccountActivationFailedMessage("Activation code incorrect")
        }
      case Blocked =>
        ToClientAccountActivationFailedMessage("This user is blocked")
      case Admin | Active =>
        ToClientAccountActivationAlreadyActiveMessage
    }
  }

  private def createNamedClientAndRouter(clientId: UUID, clientUsername: String): Unit = {
    clientUserId = clientId
    namedClient =
      context.actorOf(
        NamedClient.props(unnamedClient, self), namedClientActorName(clientId, uUIDProvider.randomUUID()))
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
