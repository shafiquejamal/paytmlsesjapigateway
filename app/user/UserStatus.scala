package user

sealed trait UserStatus {

  def value:Int

}

object UserStatus {

  case object Unverified extends UserStatus { override val value = 0 }
  case object Deactivated extends UserStatus { override val value = 1 }
  case object Active extends UserStatus { override val value = 2 }
  case object Admin extends UserStatus { override val value = 3 }
  case object Blocked extends UserStatus { override val value = 4 }

  val hasAccess = Vector[UserStatus](Active, Admin)
  val noAccess = Vector[UserStatus](Unverified, Blocked)
  val usernameAndEmailNotAvailable = Vector[UserStatus](Active, Admin, Blocked, Deactivated)
  val usernameAndEmailAvailable = Vector(Unverified)

  val allUserStatuses = Vector[UserStatus](Unverified, Deactivated, Active, Admin, Blocked)

  def toUserStatus(value:Int):UserStatus = allUserStatuses.find(_.value == value).getOrElse(Blocked)

}