package modules

import access._
import access.authentication.{AuthenticationAPI, AuthenticationFacade, PasswordResetCodeSender, PasswordResetCodeSenderImpl}
import access.registration.{AccountActivationCodeSender, AccountActivationCodeSenderImpl, RegistrationAPI, RegistrationFacade}

import com.eigenroute.scalikejdbchelpers.{ScalikeJDBCDevProdDBConfig, DBConfig, ScalikeJDBCSessionProviderImpl,
ScalikeJDBCSessionProvider}
import com.google.inject.AbstractModule
import communication.{CodeSender, CodeSenderImpl, Emailer, EmailerImpl}
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
    bind[JWTAlgorithmProvider].to[JWTAlgorithmProviderImpl]
    bind[JWTPublicKeyProvider].to[JWTPublicKeyProviderImpl]
    bind[JWTPrivateKeyProvider].to[JWTPrivateKeyProviderImpl]
    bind[Emailer].to[EmailerImpl]
    bind[AccountActivationCodeSender].to[AccountActivationCodeSenderImpl]
    bind[PasswordResetCodeSender].to[PasswordResetCodeSenderImpl]
    bind[CodeSender].to[CodeSenderImpl]
  }

}
