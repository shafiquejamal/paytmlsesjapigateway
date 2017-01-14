package user

import java.util.UUID

import com.eigenroute.id.UUIDProvider
import org.joda.time.DateTime
import user.UserStatus.Active

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
      userDAO.add(this, new DateTime(), uUIDProvider.randomUUID(), authenticationUserFilter)
    )(uUID =>
      Failure[User](new RuntimeException("This user already has a UUID."))
    )

  def this() = this(None, "", "", "", Active, None)

  def create(
      maybeId: Option[UUID],
      username: String,
      email: String,
      hashedPassword: String,
      userStatus: UserStatus,
      maybeCreated: Option[DateTime] = None):User =
    new TestUserImpl(maybeId, username, email, hashedPassword, userStatus, maybeCreated)
}
