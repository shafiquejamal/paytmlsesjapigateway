package user

import entity.User
import scalikejdbc.WrappedResultSet

trait WrappedResultSetToUserConverter {

  def converter(rs: WrappedResultSet): User
  
}
