package db

import com.google.inject.{Inject, Singleton}
import scalikejdbc.{DBSession, NamedAutoSession, ReadOnlyNamedAutoSession}

@Singleton
class ScalikeJDBCSessionProviderImpl @Inject()(dBConfig: DBConfig) extends ScalikeJDBCSessionProvider {

  override def provideReadOnlySession: DBSession = ReadOnlyNamedAutoSession(Symbol(dBConfig.dBName))

  override def provideAutoSession: DBSession = NamedAutoSession(Symbol(dBConfig.dBName))
  
}
