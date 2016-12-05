package contact.phone

import java.util.UUID

import scala.util.Try

trait PhoneDAO {

  def addPhoneNumber(forUserId: UUID, phoneNumber: PhoneNumber): Try[PhoneNumber]

  def phoneNumber(forUserId: UUID, phoneNumber: String): Option[PhoneNumber]

}
