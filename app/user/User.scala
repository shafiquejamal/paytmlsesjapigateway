package user

import java.util.UUID

import entity.ActiveFlag
import org.joda.time.DateTime

case class User (
                  maybeId: Option[UUID],
                  maybeUserName: Option[String],
                  email: String,
                  password: String,
                  override val isActive: Boolean,
                  maybeCreated:Option[DateTime] = None
          ) extends ActiveFlag[User]

object User {
  def apply(id: UUID, email: String, password: String): User = new User(Some(id), None, email, password, isActive = true)

  def apply(id: UUID, email: String, password: String, isActive: Boolean): User = {
    new User(Some(id), None, email, password, isActive)
  }

}
