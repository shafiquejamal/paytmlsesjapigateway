package user

import java.util.UUID

import entity.User
import org.joda.time.DateTime
import scalikejdbc.WrappedResultSet
import language.implicitConversions

import scala.util.{Success, Try}

case class TestUserImpl(
                         override val maybeId: Option[UUID],
                         override val maybeUserName: Option[String],
                         override val email: String,
                         override val hashedPassword: String,
                         override val isActive: Boolean,
                         override val maybeCreated:Option[DateTime] = None,
                         override val maybeParentId: Option[UUID]
                       ) extends User {

  override def add(userDAO: UserDAO):Try[User] = Success[User](this)

}

object TestUserImpl {

  implicit def wrappedResultSetToUser(rs:WrappedResultSet):User =
    TestUserImpl(
                  Option(rs.string("id")).map(UUID.fromString),
                  Option(rs.string("username")).filterNot(_.trim.isEmpty),
                  rs.string("email"),
                  rs.string("password"),
                  rs.boolean("isactive"),
                  Option(rs.jodaDateTime("created")),
                  Option(rs.string("parentid")).map(UUID.fromString)
                )

}