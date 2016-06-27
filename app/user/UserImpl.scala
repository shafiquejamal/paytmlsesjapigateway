package user

import java.util.UUID

import entity.User
import org.joda.time.DateTime
import user.UserStatus.Active
import util.UUIDProvider

import scala.util.{Failure, Try}

class UserImpl(
    override val maybeId: Option[UUID],
    override val username: String,
    override val email: String,
    override val hashedPassword: String,
    override val userStatus: UserStatus,
    override val maybeCreated: Option[DateTime] = None)
  extends User {

  override def add(
      userDAO: UserDAO,
      uUIDProvider: UUIDProvider,
      registrationUserFilter: User => Boolean,
      authenticationUserFilter: User => Boolean): Try[User] =
    maybeId.fold[Try[User]](
      userDAO.addFirstTime(this, new DateTime(), uUIDProvider.randomUUID(), registrationUserFilter, authenticationUserFilter)
    )(uUID =>
      Failure[User](new RuntimeException("This user already has a UUID."))
    )

  def this() = this(None, "", "", "", Active, None)

  override def create(
      maybeId: Option[UUID],
      username: String,
      email: String,
      hashedPassword: String,
      userStatus: UserStatus,
      maybeCreated: Option[DateTime] = None): User =
    new UserImpl(maybeId, username, email, hashedPassword, userStatus, maybeCreated)

}
