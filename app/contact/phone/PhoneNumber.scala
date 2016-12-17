package contact.phone

import java.util.UUID

import org.joda.time.DateTime

case class PhoneNumber(number: String, status: PhoneNumberStatus, registrationDate: DateTime) {
  require(number.length == 11)
  require(number.take(1) == "1")
}


case class RegisteredPhoneNumber(id: UUID, phoneNumber: PhoneNumber)