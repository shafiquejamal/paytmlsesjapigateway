package user

import java.util.UUID

import scalikejdbc.WrappedResultSet

class WrappedResultSetToTestUserConverterImpl extends WrappedResultSetToUserConverter {

  override def converter(rs: WrappedResultSet):User =
    TestUserImpl(
        Option(rs.string("id")).map(UUID.fromString),
        rs.string("username"),
        rs.string("email"),
        rs.string("password"),
        UserStatus.toUserStatus(rs.int("status")),
        Option(rs.jodaDateTime("createdat")))

}
