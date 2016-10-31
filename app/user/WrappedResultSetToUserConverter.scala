package user

import java.util.UUID

import scalikejdbc.WrappedResultSet
import user.UserStatus._

trait WrappedResultSetToUserConverter {

  def converter(rs: WrappedResultSet): User

  def toSingleUseToken(rs: WrappedResultSet): SingleUseToken =
    SingleUseToken(
      UUID.fromString(rs.string("xuserid")),
      rs.jodaDateTime("iat"))

  def toStatusOfUser(rs: WrappedResultSet): StatusOfUser =
    StatusOfUser(
      UUID.fromString(rs.string("xuserid")),
      toUserStatus(rs.int("status")),
      rs.jodaDateTime("createdat")
    )

}
