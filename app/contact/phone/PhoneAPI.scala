package contact.phone

import java.util.UUID

import scala.util.Try

trait PhoneAPI {

  def registerPhoneNumber(forUser: UUID, phoneNumberRegistration: PhoneNumberRegistrationMessage): Try[PhoneNumber]

}


