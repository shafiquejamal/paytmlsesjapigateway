package user

import java.util.UUID

import scala.language.implicitConversions
import scala.util.Try

case class UserMessage(
    override val maybeId: Option[UUID],
    override val username: String,
    override val email: String,
    override val userStatus: UserStatus)
  extends UserFields

object UserMessage {

  implicit def userToUserMessage(user:User):UserMessage =
    UserMessage(user.maybeId, user.username, user.email, user.userStatus)

  implicit def possibleUserToPossibleUserMessage(possibleUser:Try[User]):Try[UserMessage] =
    possibleUser.map( user => userToUserMessage(user))

  implicit def maybeUsertoMaybeUserMessage(maybeUser:Option[User]):Option[UserMessage] =
    maybeUser.map( user => userToUserMessage(user))

}