package user

import java.util.UUID

import com.eigenroute.id.UUIDProvider
import org.joda.time.DateTime

import scala.util.Try

trait User extends UserFields {

  def hashedPassword: String
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
