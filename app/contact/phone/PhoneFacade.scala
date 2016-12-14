package contact.phone

import java.util.UUID

import com.google.inject.Inject
import contact.phone.PhoneNumberStatus.Unverified
import util.TimeProvider

import scala.util.Try

class PhoneFacade @Inject() (phoneDAO: PhoneDAO, timeProvider: TimeProvider)  extends PhoneAPI {

  override def registerPhoneNumber(forUser: UUID, phoneNumberRegistration: PhoneNumberRegistrationMessage):
  Try[PhoneNumber] = {
    val phoneNumber = PhoneNumber(phoneNumberRegistration.phoneNumberToAdd, Unverified, timeProvider.now())
    phoneDAO.addPhoneNumber(forUser, phoneNumber)
  }

}
