package user

import java.util.UUID

import entity.ActiveFlag
import org.joda.time.DateTime

case class User (
                  maybeId: Option[UUID],
                  maybeUserName: Option[String],
                  email: String,
                  hashedPassword: String,
                  override val isActive: Boolean,
                  maybeCreated:Option[DateTime] = None,
                  maybeParentId: Option[UUID]
          ) extends ActiveFlag[User]

object User {
  def apply(id: UUID, email: String, hashedPassword: String): User = new User(Some(id), None, email, hashedPassword, isActive = true, None, None)

  def apply(id: UUID, email: String, hasedPassword: String, isActive: Boolean): User = {
    new User(Some(id), None, email, hasedPassword, isActive, None, None)
  }

}
