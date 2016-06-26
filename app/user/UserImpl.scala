package user

import java.util.UUID

import entity.User
import org.joda.time.DateTime
import util.UUIDProvider

import scala.util.{Failure, Try}

class UserImpl(
    override val maybeId: Option[UUID],
    override val username: String,
    override val email: String,
    override val hashedPassword: String,
    override val isActive: Boolean,
    override val maybeCreated: Option[DateTime] = None)
  extends User {

  override def add(userDAO: UserDAO, uUIDProvider: UUIDProvider): Try[User] =
    maybeId.fold[Try[User]](
      userDAO.addFirstTime(this, new DateTime(), uUIDProvider.randomUUID())
    )(uUID =>
      Failure[User](new RuntimeException("This user already has a UUID."))
    )

  def this() = this(None, "", "", "", false, None)

  override def create(
      maybeId: Option[UUID],
      username: String,
      email: String,
      hashedPassword: String,
      isActive: Boolean,
      maybeCreated: Option[DateTime] = None): User =
    new UserImpl(maybeId, username, email, hashedPassword, isActive=isActive, maybeCreated)

}
