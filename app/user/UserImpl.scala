package user

import java.util.UUID

import entity.User
import org.joda.time.DateTime
import scalikejdbc.WrappedResultSet
import language.implicitConversions

import scala.util.{Failure, Try}

case class UserImpl (
                  override val maybeId: Option[UUID],
                  override val maybeUserName: Option[String],
                  override val email: String,
                  override val hashedPassword: String,
                  override val isActive: Boolean,
                  override val maybeCreated:Option[DateTime] = None,
                  override val maybeParentId: Option[UUID]
          ) extends User {

  override def add(userDAO: UserDAO):Try[User] = {
    maybeId.fold[Try[User]](
      new UserCreator(userDAO, this).signUp()
                )( uUID =>
      Failure[User](new RuntimeException("This user already has a UUID."))
                 )
  }

}

object UserImpl {
  def apply(id: UUID, email: String, hashedPassword: String): UserImpl =
    new UserImpl(Some(id), None, email, hashedPassword, isActive = true, None, None)

  def apply(id: UUID, email: String, hasedPassword: String, isActive: Boolean): UserImpl = {
    new UserImpl(Some(id), None, email, hasedPassword, isActive, None, None)
  }

  implicit def wrappedResultSetToUser(rs:WrappedResultSet):User = {
    new UserImpl(
                  maybeId = Option(rs.string("id")).map(UUID.fromString),
                  maybeUserName = Option(rs.string("username")).filterNot(_.trim.isEmpty),
                  email = rs.string("email"),
                  hashedPassword = rs.string("password"),
                  isActive = rs.boolean("isactive"),
                  maybeCreated = Option(rs.jodaDateTime("created")),
                  maybeParentId = Option(rs.string("parentid")).map(UUID.fromString)
                )
  }

}
