package contact.phone

import org.joda.time.DateTime

case class PhoneNumber(number: String, status: PhoneNumberStatus, registrationDate: DateTime) {
  require(number.length == 11)
  require(number.take(1) == "1")
}
