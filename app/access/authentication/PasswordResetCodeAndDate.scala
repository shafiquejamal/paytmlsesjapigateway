package access.authentication

import org.joda.time.DateTime
import scalikejdbc.WrappedResultSet

case class PasswordResetCodeAndDate(code: String, date: DateTime) {

  require(code.trim.nonEmpty)
  require(Option(date).isDefined)

}

object PasswordResetCodeAndDate {

  def converter(rs: WrappedResultSet): PasswordResetCodeAndDate =
    PasswordResetCodeAndDate(rs.string("passwordresetcode"), rs.jodaDateTime("createdat"))

}