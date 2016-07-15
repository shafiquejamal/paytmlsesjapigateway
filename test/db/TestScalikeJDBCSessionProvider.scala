package db

import scalikejdbc.DBSession

class TestScalikeJDBCSessionProvider(session: DBSession) extends ScalikeJDBCSessionProvider {

  override def provideReadOnlySession = session

  override def provideAutoSession = session

}

object TestScalikeJDBCSessionProvider {

  def apply(session: DBSession) = new TestScalikeJDBCSessionProvider(session)

}


