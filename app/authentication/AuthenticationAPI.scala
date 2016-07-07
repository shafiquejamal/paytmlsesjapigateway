package authentication

import java.util.UUID

import user.User

trait AuthenticationAPI {

  def user(id: UUID): Option[User]

  def user(authenticationMessage: AuthenticationMessage): Option[User]
  
}
