package entrypoint

import access.{AuthenticatedActionCreator, JWTAlgorithmProvider, JWTPublicKeyProvider}
import com.eigenroute.time.TimeProvider
import com.google.inject.Inject
import play.api.Configuration

class UserChecker @Inject()(
    override val authenticationAPI:AuthenticationAPI,
    override val jWTAlgorithmProvider: JWTAlgorithmProvider,
    override val jWTPublicKeyProvider: JWTPublicKeyProvider,
    override val configuration: Configuration,
    override val timeProvider: TimeProvider)
  extends AuthenticatedActionCreator {

}
