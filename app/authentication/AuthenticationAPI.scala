package authentication

import java.util.UUID

import entity.User
import user.UserMessage

trait AuthenticationAPI {

  def user(parentId: UUID): Option[User]

  def user(userMessage: UserMessage, hashedPassword: String): Option[User]
  
}
