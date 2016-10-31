package access

import java.util.UUID

import access.authentication.AuthenticationAPI
import org.joda.time.DateTime
import user.UserMessage

sealed trait TokenUse {

  def validate(api: AuthenticationAPI, userId: UUID, iat: DateTime): Option[UserMessage]

}

case object SingleUse extends TokenUse {
  override def validate(api: AuthenticationAPI, userId: UUID, iat: DateTime): Option[UserMessage] =
    api.validateOneTime(userId, iat)
}

case object MultiUse extends TokenUse {
  override def validate(api: AuthenticationAPI, userId: UUID, iat: DateTime): Option[UserMessage] =
    api.userById(userId)
}

case class AllowedTokens(tokens: Vector[TokenUse]) {
  require(tokens.distinct.size == tokens.size)
  require(tokens.nonEmpty)
}

object AllowedTokens {
  def apply(tokenUse: TokenUse): AllowedTokens = AllowedTokens(Vector(tokenUse))
}

object TokenUse {
  val fromDescription = Map[String, TokenUse]("single" -> SingleUse, "multi" -> MultiUse)
}
