package modules

import access.authentication.{AuthenticationAPI, AuthenticationFacade}
import access.registration.{AccountActivator, AccountActivatorImpl, RegistrationAPI, RegistrationFacade}
import access.{JWTParamsProvider, JWTParamsProviderImpl}
import com.google.inject.AbstractModule
import communication.{Emailer, EmailerImpl}
import db.{DBConfig, ScalikeJDBCDevProdDBConfig, ScalikeJDBCSessionProvider, ScalikeJDBCSessionProviderImpl}
import net.codingwell.scalaguice.ScalaModule
import user.{UserAPI, UserFacade}
import util._

class Module extends AbstractModule with ScalaModule {

  override def configure() {
    bind[ScalikeJDBCSessionProvider].to[ScalikeJDBCSessionProviderImpl]
    bind[TimeProvider].to[TimeProviderImpl]
    bind[UUIDProvider].to[UUIDProviderImpl]
    bind[RegistrationAPI].to[RegistrationFacade]
    bind[AuthenticationAPI].to[AuthenticationFacade]
    bind[UserAPI].to[UserFacade]
    bind[DBConfig].to[ScalikeJDBCDevProdDBConfig]
    bind[ConfigParamsProvider].to[PlayConfigParamsProvider]
    bind[JWTParamsProvider].to[JWTParamsProviderImpl]
    bind[Emailer].to[EmailerImpl]
    bind[AccountActivator].to[AccountActivatorImpl]
  }

}
