package user

import java.util.UUID

import org.joda.time.DateTime

case class StatusOfUser(uUID: UUID, override val userStatus: UserStatus, statusCreatedAt: DateTime) extends StatusField