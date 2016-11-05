package chat

import java.util.UUID

import com.google.inject.Inject
import contact.ChatContactDAO
import util.TimeProvider

import scala.util.Try

trait ChatContactAPI {

  def visibleContactsFor(userId: UUID): Seq[String]

  def addContact(forUserId: UUID, contactUserId: UUID): Try[UUID]

}

class ChatContactFacade @Inject()(
    contactDAO: ChatContactDAO,
    timeProvider: TimeProvider) extends ChatContactAPI {

  override def visibleContactsFor(userId: UUID): Seq[String] =
    contactDAO.visibleContactsFor(userId).map(_.contactUsername)

  override def addContact(forUserId: UUID, contactUserId: UUID): Try[UUID] =
    contactDAO.addContact(forUserId, contactUserId, timeProvider.now())

}
