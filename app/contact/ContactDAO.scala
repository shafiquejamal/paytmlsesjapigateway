package contact

import java.util.UUID

import com.google.inject.Inject
import contact.ContactVisibility.Visible
import db.{DBConfig, ScalikeJDBCSessionProvider}
import org.joda.time.DateTime
import scalikejdbc._
import util.UUIDProvider

import scala.util.{Failure, Success, Try}

trait ContactDAO {

  def visibleContactsFor(userId: UUID): Seq[Contact]

  def addContact(forXuserId: UUID, contactXuserIdToAdd: UUID, createdAt: DateTime): Try[UUID]

}

class ContactDAOImpl @Inject() (
    scalikeJDBCSessionProvider: ScalikeJDBCSessionProvider,
    dBConfig: DBConfig,
    uUIDProvider: UUIDProvider)
  extends ContactDAO {

  val namedDB = NamedDB(Symbol(dBConfig.dBName))
  namedDB.autoClose(false)

  override def visibleContactsFor(userId: UUID): Seq[Contact] = {

    implicit val readOnlySession = scalikeJDBCSessionProvider.provideReadOnlySession

    sql"""select DISTINCT ON (contact.id) contactxuserid, username, contactvisibility.visibility
         from contact
         join contactvisibility on contactvisibility.contactid = contact.id
         join xuserusername on xuserusername.xuserid = contactxuserid
         where contact.xuserid = $userId
         order by contact.id desc, contactvisibility.createdat desc"""
    .map(WrappedResultSetToContactWithVisibilityConverter.convert)
    .list().apply().filter(_.contactVisibility == Visible).map(_.contact)

  }

  override def addContact(forXuserId: UUID, contactXuserIdToAdd: UUID, createdAt: DateTime): Try[UUID] = {

    implicit val session = scalikeJDBCSessionProvider.provideAutoSession

      val nRecords =
        sql"""select id, xuserid, contactxuserid from contact where xuserid = $forXuserId and contactxuserid = $contactXuserIdToAdd
             order by createdat limit 1
           """.map(
        rs => rs.string("id")
      ).single().apply()

      nRecords.fold[Try[UUID]] {
        val contactId = uUIDProvider.randomUUID()
        val queryIntoContactTable =
          sql"""insert into contact (id, xuserid, contactxuserid, createdat) VALUES
          ($contactId, $forXuserId, $contactXuserIdToAdd, $createdAt)
          """.update().apply()
        if (queryIntoContactTable == 1) {
          val queryIntoVisibility =
            sql"""insert into contactvisibility (id, contactid, visibility, createdat) VALUES
            (${uUIDProvider.randomUUID()}, $contactId, ${Visible.number}, $createdAt)""".update().apply()
          if (queryIntoVisibility == 1) {
            Success(contactXuserIdToAdd)
          } else {
            Failure(new Exception("Could not make contact visible"))
          }
        } else {
          Failure(new Exception("Cound not insert into contact table"))
        }
      } { record =>
        val recordId = UUID.fromString(record)
        val contactIsAlreadyVisible =
          sql"""select DISTINCT ON (contact.id) contactxuserid, username, contactvisibility.visibility
            from contact
            join contactvisibility on contactvisibility.contactid = contact.id
            join xuserusername on xuserusername.xuserid = contactxuserid
            where contact.xuserid = $forXuserId and contactxuserid = $contactXuserIdToAdd
            order by contact.id desc, contactvisibility.createdat desc"""
          .map(WrappedResultSetToContactWithVisibilityConverter.convert)
          .list().apply().filter(_.contactVisibility == Visible).map(_.contact) == 1

        if (! contactIsAlreadyVisible) {
          val query =
            sql"""insert into contactvisibility (id, contactid, visibility, createdat) VALUES
                 (${uUIDProvider.randomUUID()}, $recordId, ${Visible.number}, $createdAt)
               """.update().apply()
          if (query == 1)
            Success(contactXuserIdToAdd)
          else
            Failure(new Exception("Could not make contact visible"))
        } else {
          Success(contactXuserIdToAdd)
        }
      }

    }




}