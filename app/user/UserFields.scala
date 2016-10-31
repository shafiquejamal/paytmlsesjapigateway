package user

import java.util.UUID

trait UserFields extends StatusField {

  def maybeId: Option[UUID]
  def username: String
  def email: String

}

trait StatusField {

  def userStatus: UserStatus

}