package socket

import access.authentication.AuthenticationAPI
import access.{AuthenticatedActionCreator, JWTAlgorithmProvider, JWTPublicKeyProvider}
import com.google.inject.Inject
import play.api.Configuration
import util.TimeProvider

class SocketAuthenticator @Inject()(
    override val authenticationAPI:AuthenticationAPI,
    override val jWTAlgorithmProvider: JWTAlgorithmProvider,
    override val jWTPublicKeyProvider: JWTPublicKeyProvider,
    override val configuration: Configuration,
    override val timeProvider: TimeProvider)
  extends AuthenticatedActionCreator {

}
