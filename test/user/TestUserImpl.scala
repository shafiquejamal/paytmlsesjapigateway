package user

import java.util.UUID

import entity.User
import org.joda.time.DateTime
import user.UserStatus.Blocked
import util.UUIDProvider

import scala.util.{Failure, Try}

case class TestUserImpl(
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

  def this() = this(None, "", "", "", Blocked, None)

  def create(
      maybeId: Option[UUID],
      username: String,
      email: String,
      hashedPassword: String,
      userStatus: UserStatus,
      maybeCreated: Option[DateTime] = None):User =
    new TestUserImpl(maybeId, username, email, hashedPassword, userStatus, maybeCreated)
}
