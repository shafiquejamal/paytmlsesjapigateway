package modules

import access._
import access.authentication.{AuthenticationFacade, PasswordResetCodeSender, PasswordResetCodeSenderImpl}
import access.registration.{AccountActivationCodeSender, AccountActivationCodeSenderImpl, RegistrationFacade}
import asynccommunication.{Emailer, EmailerImpl}
import com.eigenroute.id.{UUIDProvider, UUIDProviderImpl}
import com.eigenroute.scalikejdbchelpers.{DBConfig, ScalikeJDBCDevProdDBConfig, ScalikeJDBCSessionProvider, ScalikeJDBCSessionProviderImpl}
import com.eigenroute.time.{TimeProvider, TimeProviderImpl}
import com.google.inject.AbstractModule
import entrypoint.{AuthenticationAPI, RegistrationAPI, UserAPI}
import net.codingwell.scalaguice.ScalaModule
import user.UserFacade

class Module extends AbstractModule with ScalaModule {

  override def configure() {
    bind[ScalikeJDBCSessionProvider].to[ScalikeJDBCSessionProviderImpl]
    bind[TimeProvider].to[TimeProviderImpl]
    bind[UUIDProvider].to[UUIDProviderImpl]
    bind[RegistrationAPI].to[RegistrationFacade]
    bind[AuthenticationAPI].to[AuthenticationFacade]
    bind[UserAPI].to[UserFacade]
    bind[DBConfig].to[ScalikeJDBCDevProdDBConfig]
    bind[JWTAlgorithmProvider].to[JWTAlgorithmProviderImpl]
    bind[JWTPublicKeyProvider].to[JWTPublicKeyProviderImpl]
    bind[JWTPrivateKeyProvider].to[JWTPrivateKeyProviderImpl]
    bind[Emailer].to[EmailerImpl]
    bind[AccountActivationCodeSender].to[AccountActivationCodeSenderImpl]
    bind[PasswordResetCodeSender].to[PasswordResetCodeSenderImpl]
    bind[CodeSender].to[CodeSenderImpl]
  }

}
