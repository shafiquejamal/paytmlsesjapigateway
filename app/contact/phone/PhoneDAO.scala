package contact.phone

import java.util.UUID

import scala.util.Try

trait PhoneDAO {

  def addPhoneNumber(forUserId: UUID, phoneNumber: PhoneNumber): Try[UUID]

  def phoneNumber(forUserId: UUID): Option[RegisteredPhoneNumber]

}
