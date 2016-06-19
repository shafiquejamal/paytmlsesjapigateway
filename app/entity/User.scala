package entity

import java.util.UUID

import org.joda.time.DateTime
import user.UserDAO

import scala.util.Try

trait User {

  def maybeId: Option[UUID]
  def maybeUserName: Option[String]
  def email: String
  def hashedPassword: String
  def isActive: Boolean
  def maybeCreated:Option[DateTime] = None
  def maybeParentId: Option[UUID]

  def add(userDAO: UserDAO):Try[User]

  def create(
              maybeId: Option[UUID],
              maybeUserName: Option[String],
              email: String,
              hashedPassword: String,
              isActive: Boolean,
              maybeCreated: Option[DateTime] = None,
              maybeParentId: Option[UUID]
            ):User

}
