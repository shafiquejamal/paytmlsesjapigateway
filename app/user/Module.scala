package user

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

class Module extends AbstractModule with ScalaModule {

  override def configure() {
    bind[UserDAO].to[ScalikeJDBCUserDAO]
    bind[UserAPI].to[UserFacade]
  }

}
