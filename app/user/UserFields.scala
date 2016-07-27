package user

import java.util.UUID

trait UserFields {

  def maybeId: Option[UUID]
  def username: String
  def email: String
  def userStatus: UserStatus

}
