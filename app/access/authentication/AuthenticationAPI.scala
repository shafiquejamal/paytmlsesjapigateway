package access.authentication

import java.util.UUID

import user.User

trait AuthenticationAPI {

  def userById(id: UUID): Option[User]

  def user(authenticationMessage: AuthenticationMessage): Option[User]
  
}
