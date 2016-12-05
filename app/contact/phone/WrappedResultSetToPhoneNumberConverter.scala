package contact.phone

import scalikejdbc.WrappedResultSet

object WrappedResultSetToPhoneNumberConverter {

  def convert(rs: WrappedResultSet): PhoneNumber =
    PhoneNumber(
      rs.string("phonenumber"),
      PhoneNumberStatus.toPhoneNumberStatus(rs.int("status")),
      rs.jodaDateTime("createdat"))

}

