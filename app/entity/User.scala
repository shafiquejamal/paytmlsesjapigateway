package entity

import java.util.UUID

import org.joda.time.DateTime
import user.{UserDAO, UserStatus}
import util.UUIDProvider

import scala.util.Try

trait User {

  def maybeId: Option[UUID]
  def username: String
  def email: String
  def hashedPassword: String
  def userStatus: UserStatus
  def maybeCreated:Option[DateTime] = None

  def add(
      userDAO: UserDAO,
      uUIDProvider: UUIDProvider,
      registrationUserFilter: User => Boolean,
      authenticationUserFilter: User => Boolean):Try[User]

  def create(
      maybeId: Option[UUID],
      username: String,
      email: String,
      hashedPassword: String,
      userStatus: UserStatus,
      maybeCreated: Option[DateTime] = None):User

}
