package contact

import java.util.UUID

import scalikejdbc.WrappedResultSet

object WrappedResultSetToContactWithVisibilityConverter {

  def convert(rs: WrappedResultSet): ContactWithVisibility =
    ContactWithVisibility(
      Contact(UUID.fromString(rs.string("contactxuserid")), rs.string("username")),
        ContactVisibility.from(rs.int("visibility")))

}
