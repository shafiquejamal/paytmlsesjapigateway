package entity

import java.util.UUID

import org.joda.time.DateTime
import user.UserDAO
import util.UUIDProvider

import scala.util.Try

trait User {

  def maybeId: Option[UUID]
  def username: String
  def email: String
  def hashedPassword: String
  def isActive: Boolean
  def maybeCreated:Option[DateTime] = None

  def add(userDAO: UserDAO, uUIDProvider: UUIDProvider):Try[User]

  def create(
      maybeId: Option[UUID],
      username: String,
      email: String,
      hashedPassword: String,
      isActive: Boolean,
      maybeCreated: Option[DateTime] = None):User

}
