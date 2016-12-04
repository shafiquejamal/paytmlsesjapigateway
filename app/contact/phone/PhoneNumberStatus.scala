package contact.phone

sealed trait PhoneNumberStatus {

  def value: Int

}

object PhoneNumberStatus {

  case object Unverified extends PhoneNumberStatus { override val value = 0 }
  case object Verified extends PhoneNumberStatus { override val value = 1 }
  case object Removed extends PhoneNumberStatus { override val value = 2 }

  private val allPhoneNumberStatuses = Seq(Unverified, Verified, Removed)

  def toPhoneNumberStatus(value: Int): PhoneNumberStatus = allPhoneNumberStatuses.find(_.value == value).getOrElse(Removed)

}