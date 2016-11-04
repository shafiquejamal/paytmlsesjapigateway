package contact

import java.util.UUID

import scalikejdbc.WrappedResultSet

object WrappedResultSetToContactWithVisibilityConverter {

  def convert(rs: WrappedResultSet): ChatContactWithVisibility =
    ChatContactWithVisibility(
      ChatContact(UUID.fromString(rs.string("contactxuserid")), rs.string("username")),
        ChatContactVisibility.from(rs.int("visibility")))

}
