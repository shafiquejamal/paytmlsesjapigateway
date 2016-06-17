package user

import java.util.UUID

import entity.User
import org.joda.time.DateTime

import scala.util.{Success, Try}

case class TestUserImpl(
                         override val maybeId: Option[UUID],
                         override val maybeUserName: Option[String],
                         override val email: String,
                         override val hashedPassword: String,
                         override val isActive: Boolean,
                         override val maybeCreated: Option[DateTime] = None,
                         override val maybeParentId: Option[UUID]
                       ) extends User {

  override def add(userDAO: UserDAO): Try[User] = Success[User](this)

}
