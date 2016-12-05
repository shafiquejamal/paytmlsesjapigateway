package contact.phone

import java.util.UUID

import com.google.inject.Inject
import db.{DBConfig, ScalikeJDBCSessionProvider}
import scalikejdbc._
import util.UUIDProvider

import scala.util._


class ScalikeJDBCPhoneDAOImpl @Inject()(
    scalikeJDBCSessionProvider: ScalikeJDBCSessionProvider,
    dBConfig: DBConfig,
    uUIDProvider: UUIDProvider)
  extends PhoneDAO {

  val namedDB = NamedDB(Symbol(dBConfig.dBName))
  namedDB.autoClose(false)

  override def addPhoneNumber(forUserId: UUID, phoneNumber: PhoneNumber): Try[PhoneNumber] = {
    implicit val session = scalikeJDBCSessionProvider.provideAutoSession
    val id = uUIDProvider.randomUUID()
    val query =
      sql"""insert into xuserphonenumber (id, xuserid, phonenumber, status, createdat) values
           (${id}, ${forUserId}, ${phoneNumber.number}, ${phoneNumber.status.value}, ${phoneNumber.registrationDate})"""
      .update().apply()
    if (query == 1)
      Success(phoneNumber)
    else
      Failure(new Exception("Could not add phone number"))
  }

  override def phoneNumber(forXuserId: UUID, phoneNumber: String): Option[PhoneNumber] = {
    implicit val session = scalikeJDBCSessionProvider.provideAutoSession
    sql"""select phonenumber, status, createdat from xuserphonenumber where xuserid = $forXuserId AND
           phonenumber = ${phoneNumber} ORDER BY createdat DESC limit 1"""
    .map(WrappedResultSetToPhoneNumberConverter.convert).single.apply()
  }

}
