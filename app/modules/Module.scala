package modules

import authentication.{AuthenticationAPI, AuthenticationFacade}
import com.google.inject.AbstractModule
import db.{DBConfig, ScalikeJDBCDevProdDBConfig, ScalikeJDBCSessionProvider, ScalikeJDBCSessionProviderImpl}
import net.codingwell.scalaguice.ScalaModule
import registration.{RegistrationAPI, RegistrationFacade}
import util._

class Module extends AbstractModule with ScalaModule {

  override def configure() {
    bind[ScalikeJDBCSessionProvider].to[ScalikeJDBCSessionProviderImpl]
    bind[TimeProvider].to[TimeProviderImpl]
    bind[UUIDProvider].to[UUIDProviderImpl]
    bind[RegistrationAPI].to[RegistrationFacade]
    bind[AuthenticationAPI].to[AuthenticationFacade]
    bind[DBConfig].to[ScalikeJDBCDevProdDBConfig]
    bind[ConfigParamsProvider].to[PlayConfigParamsProvider]
  }

}
