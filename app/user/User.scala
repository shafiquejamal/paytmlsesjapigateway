package user

import entity.Active
import org.joda.time.DateTime

case class User (
                  id: Option[String],
                  userName: Option[String],
                  email: String,
                  password: String,
                  override val isActive: Boolean,
                  created:Option[DateTime] = None
          ) extends Active[User]

object User {
  def apply(id: String, email: String, password: String): User = new User(Some(id), None, email, password, isActive = true)

  def apply(id: String, email: String, password: String, isActive: Boolean): User = {
    new User(Some(id), None, email, password, isActive)
  }

}
