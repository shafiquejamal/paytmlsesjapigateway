package db

import scalikejdbc.DBSession

trait ScalikeJDBCSessionProvider {

  def provideReadOnlySession: DBSession

  def provideAutoSession: DBSession
  
}
