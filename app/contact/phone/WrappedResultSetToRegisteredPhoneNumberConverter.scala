package contact.phone

import java.util.UUID

import scalikejdbc.WrappedResultSet

object WrappedResultSetToRegisteredPhoneNumberConverter {

  def convert(rs: WrappedResultSet): RegisteredPhoneNumber =
    RegisteredPhoneNumber(UUID.fromString(rs.string("id")), PhoneNumber(
      rs.string("phonenumber"),
      PhoneNumberStatus.toPhoneNumberStatus(rs.int("status")),
      rs.jodaDateTime("createdat")))

}

