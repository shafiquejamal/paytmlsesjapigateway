package user

import java.util.UUID

import entity.User
import scalikejdbc.WrappedResultSet

class WrappedResultSetToUserConverterImpl extends WrappedResultSetToUserConverter {

  override def converter(rs: WrappedResultSet):User = UserImpl(
    Option(rs.string("id")).map(UUID.fromString),
    Option(rs.string("username")).filterNot(_.trim.isEmpty),
    rs.string("email"),
    rs.string("password"),
    rs.boolean("isactive"),
    Option(rs.jodaDateTime("created")),
    Option(rs.string("parentid")).map(UUID.fromString)
  )

}
