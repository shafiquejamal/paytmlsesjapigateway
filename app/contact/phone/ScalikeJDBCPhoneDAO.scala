package contact.phone

import java.util.UUID

import com.google.inject.Inject
import db.{DBConfig, ScalikeJDBCSessionProvider}
import scalikejdbc._
import util.UUIDProvider

import scala.util._


class ScalikeJDBCPhoneDAO @Inject()(
    scalikeJDBCSessionProvider: ScalikeJDBCSessionProvider,
    dBConfig: DBConfig,
    uUIDProvider: UUIDProvider)
  extends PhoneDAO {

  val namedDB = NamedDB(Symbol(dBConfig.dBName))
  namedDB.autoClose(false)

  override def addPhoneNumber(forUserId: UUID, phoneNumber: PhoneNumber): Try[UUID] = {
    implicit val session = scalikeJDBCSessionProvider.provideAutoSession
    val id = uUIDProvider.randomUUID()
    val query =
      sql"""insert into xuserphonenumber (id, xuserid, phonenumber, status, createdat) values
           (${id}, ${forUserId}, ${phoneNumber.number}, ${phoneNumber.status.value}, ${phoneNumber.registrationDate})"""
      .update().apply()
    if (query == 1)
      Success(id)
    else
      Failure(new Exception("Could not add phone number"))
  }

  override def phoneNumber(forXuserId: UUID): Option[RegisteredPhoneNumber] = {
    implicit val session = scalikeJDBCSessionProvider.provideAutoSession
    sql"""select id, phonenumber, status, createdat from xuserphonenumber where xuserid = $forXuserId
          ORDER BY createdat DESC limit 1"""
    .map(WrappedResultSetToRegisteredPhoneNumberConverter.convert).single.apply()
  }

}
