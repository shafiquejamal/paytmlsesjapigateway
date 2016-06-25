package db

import com.google.inject.Singleton
import scalikejdbc.{AutoSession, DBSession, ReadOnlyAutoSession}

@Singleton
class ScalikeJDBCSessionProviderImpl extends ScalikeJDBCSessionProvider {

  override def provideReadOnlySession: DBSession = ReadOnlyAutoSession

  override def provideAutoSession: DBSession = AutoSession
  
}
