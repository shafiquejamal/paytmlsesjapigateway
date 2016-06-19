package user

import com.google.inject.AbstractModule
import entity.User
import net.codingwell.scalaguice.ScalaModule

class Module extends AbstractModule with ScalaModule {

  override def configure() {
    bind[UserDAO].to[ScalikeJDBCUserDAO]
    bind[WrappedResultSetToUserConverter].to[WrappedResultSetToUserConverterImpl]
    bind[User].to[UserImpl]
  }

}
