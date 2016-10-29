package chat

import access.{AuthenticatedActionCreator, JWTParamsProvider}
import access.authentication.AuthenticationAPI
import com.google.inject.Inject
import play.Configuration
import util.TimeProvider

class ChatAuthenticator @Inject() (
    override val authenticationAPI:AuthenticationAPI,
    override val jWTParamsProvider: JWTParamsProvider,
    override val configuration: Configuration,
    override val timeProvider: TimeProvider)
  extends AuthenticatedActionCreator {

}
