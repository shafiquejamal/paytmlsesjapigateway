package user

import java.util.UUID

import entity.User
import scalikejdbc.WrappedResultSet

class WrappedResultSetToTestUserConverterImpl extends WrappedResultSetToUserConverter {

  override def converter(rs: WrappedResultSet):User =
    TestUserImpl(
        Option(rs.string("id")).map(UUID.fromString),
        rs.string("username"),
        rs.string("email"),
        rs.string("password"),
        rs.boolean("isactive"),
        Option(rs.jodaDateTime("created")),
        Option(rs.string("parentid")).map(UUID.fromString))

}
