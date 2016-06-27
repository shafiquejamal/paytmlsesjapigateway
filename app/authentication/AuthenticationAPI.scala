package authentication

import java.util.UUID

import entity.User

trait AuthenticationAPI {

  def user(parentId: UUID): Option[User]

  def user(authenticationMessage: AuthenticationMessage): Option[User]
  
}
