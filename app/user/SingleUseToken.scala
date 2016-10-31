package user

import java.util.UUID

import org.joda.time.DateTime

case class SingleUseToken(userId: UUID, iat: DateTime)
