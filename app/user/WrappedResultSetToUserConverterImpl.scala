package user

import java.util.UUID

import com.google.inject.Inject
import entity.User
import scalikejdbc.WrappedResultSet

class WrappedResultSetToUserConverterImpl @Inject() (user:User) extends WrappedResultSetToUserConverter {

  override def converter(rs: WrappedResultSet):User = user.create(
    Option(rs.string("id")).map(UUID.fromString),
    rs.string("username"),
    rs.string("email"),
    rs.string("password"),
    rs.boolean("isactive"),
    Option(rs.jodaDateTime("created")),
    Option(rs.string("parentid")).map(UUID.fromString)
  )

}
