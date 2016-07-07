package user

import scalikejdbc.WrappedResultSet

trait WrappedResultSetToUserConverter {

  def converter(rs: WrappedResultSet): User
  
}
