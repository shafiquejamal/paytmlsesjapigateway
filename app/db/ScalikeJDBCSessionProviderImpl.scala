package db

import scalikejdbc.{AutoSession, DBSession, ReadOnlyAutoSession}

class ScalikeJDBCSessionProviderImpl extends ScalikeJDBCSessionProvider {

  override def provideReadOnlySession: DBSession = ReadOnlyAutoSession

  override def provideAutoSession: DBSession = AutoSession
  
}
