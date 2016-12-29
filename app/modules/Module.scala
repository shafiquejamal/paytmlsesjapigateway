package modules

import access.authentication.{PasswordResetCodeSenderImpl, PasswordResetCodeSender, AuthenticationAPI, AuthenticationFacade}
import access.registration.{AccountActivationCodeSender, AccountActivationCodeSenderImpl, RegistrationAPI,
RegistrationFacade}
import access.{JWTKeysProvider, JWTKeysProviderImpl}
import com.google.inject.AbstractModule
import communication.{CodeSenderImpl, CodeSender, Emailer, EmailerImpl}
import contact.phone._
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
    bind[PhoneAPI].to[PhoneFacade]
    bind[DBConfig].to[ScalikeJDBCDevProdDBConfig]
    bind[JWTKeysProvider].to[JWTKeysProviderImpl]
    bind[Emailer].to[EmailerImpl]
    bind[AccountActivationCodeSender].to[AccountActivationCodeSenderImpl]
    bind[PasswordResetCodeSender].to[PasswordResetCodeSenderImpl]
    bind[CodeSender].to[CodeSenderImpl]
    bind[PhoneDAO].to[ScalikeJDBCPhoneDAO]
    bind[PhoneVerificationCodeSender].to[PhoneVerificationCodeSenderImpl]
  }

}
