package chat

import java.util.UUID

import chat.ChatMessageVisibility._
import com.google.inject.Inject
import db.{DBConfig, ScalikeJDBCSessionProvider}
import org.joda.time.DateTime
import scalikejdbc._

import scala.util.{Failure, Success, Try}

class ChatMessageDAOImpl @Inject() (
    scalikeJDBCSessionProvider: ScalikeJDBCSessionProvider,
    dBConfig: DBConfig) extends ChatMessageDAO {

  val namedDB = NamedDB(Symbol(dBConfig.dBName))
  namedDB.autoClose(false)

  override def add(
      chatMessage: OutgoingChatMessageWithVisibility,
      chatMessageUUID: UUID,
      createdAt: DateTime,
      visibilityUUID: UUID):
  Try[OutgoingChatMessageWithVisibility] = {

    implicit val session = scalikeJDBCSessionProvider.provideAutoSession

    val sentAt = new DateTime(chatMessage.toClientChatMessage.time)
    val insertedMessages = sql"""insert into chatmessage (id, fromxuserid, toxuserid, messagetext, createdat, sentat) VALUES
         ($chatMessageUUID, ${chatMessage.fromId}, ${chatMessage.toId}, ${chatMessage.toClientChatMessage.text},
          ${createdAt}, ${sentAt})""".update().apply()

    val maybeSenderVisibilityAdded = addSenderVisibility(chatMessageUUID, createdAt, chatMessage.senderVisibility, visibilityUUID)

    if (insertedMessages == 1 && maybeSenderVisibilityAdded.isSuccess)
      Success(chatMessage)
    else
      Failure(new Exception("Failed to add message or visibility"))
  }

  override def addSenderVisibility(
    chatMessageUUID: UUID,
    createdAt: DateTime,
    senderVisibility: ChatMessageVisibility,
    visibilityUUID: UUID): Try[UUID] = {

    implicit val session = scalikeJDBCSessionProvider.provideAutoSession
    addSenderVisibilitySession(chatMessageUUID, createdAt, senderVisibility, visibilityUUID)
  }

  override def addReceiverVisibility(
    chatMessageUUID: UUID,
    createdAt: DateTime,
    receiverVisibility: ChatMessageVisibility,
    visibilityUUID: UUID): Try[UUID] = {

    implicit val session = scalikeJDBCSessionProvider.provideAutoSession
    addReceiverVisibilitySession(chatMessageUUID, createdAt, receiverVisibility, visibilityUUID)
  }

  def addSenderVisibilitySession(
      chatMessageUUID: UUID,
      createdAt: DateTime,
      senderVisibility: ChatMessageVisibility,
      visibilityUUID: UUID)(implicit session:DBSession): Try[UUID] = {

    val insertedSenderVisibility = sql"""insert into chatmessagesendervisibility (id, chatmessageid, visibility, createdat) VALUES
      (${visibilityUUID}, ${chatMessageUUID}, ${senderVisibility.number}, $createdAt)""".update().apply()
    if (insertedSenderVisibility == 1)
      Success(visibilityUUID)
    else
      Failure(new Exception("Unable to add sender visibility"))
  }

  def addReceiverVisibilitySession(
      chatMessageUUID: UUID,
      createdAt: DateTime,
      receiverVisibility: ChatMessageVisibility,
      visibilityUUID: UUID)(implicit session:DBSession): Try[UUID] = {

    val insertedReceiverVisibility = sql"""insert into chatmessagereceivervisibility (id, chatmessageid, visibility, createdat) VALUES
      (${visibilityUUID}, ${chatMessageUUID}, ${receiverVisibility.number}, $createdAt)""".update().apply()
    if (insertedReceiverVisibility == 1)
      Success(visibilityUUID)
    else
      Failure(new Exception("Unable to add receiver visibility"))
  }

  override def visibleMessages(toOrFromXuserId: UUID, maybeAfter: Option[DateTime]): Seq[OutgoingChatMessageWithVisibility] = {
    implicit val readOnlySession = scalikeJDBCSessionProvider.provideReadOnlySession
    val query = maybeAfter.fold(sql"""select DISTINCT ON (chatmessage.id) fromxuserid, toxuserid, messagetext, sentat, chatmessage.id as chatmsgid,
          xuserusernamefrom.username as fromusername,
          xuserusernameto.username as tousername,
          chatmessagesendervisibility.visibility as sendervisibility,
          chatmessagereceivervisibility.visibility as receivervisibility
         from chatmessage
         join chatmessagesendervisibility on chatmessagesendervisibility.chatmessageid = chatmessage.id
         join chatmessagereceivervisibility on chatmessagereceivervisibility.chatmessageid = chatmessage.id
         join xuserusername as xuserusernamefrom on xuserusernamefrom.xuserid = fromxuserid
         join xuserusername as xuserusernameto on xuserusernameto.xuserid = toxuserid
         where (fromxuserid = $toOrFromXuserId OR toxuserid = $toOrFromXuserId)
         order by chatmessage.id, chatmessagesendervisibility.createdat desc,
         chatmessagereceivervisibility.createdat desc"""){ after =>
      sql"""select DISTINCT ON (chatmessage.id) fromxuserid, toxuserid, messagetext, sentat, chatmessage.id as chatmsgid,
          xuserusernamefrom.username as fromusername,
          xuserusernameto.username as tousername,
          chatmessagesendervisibility.visibility as sendervisibility,
          chatmessagereceivervisibility.visibility as receivervisibility
         from chatmessage
         join chatmessagesendervisibility on chatmessagesendervisibility.chatmessageid = chatmessage.id
         join chatmessagereceivervisibility on chatmessagereceivervisibility.chatmessageid = chatmessage.id
         join xuserusername as xuserusernamefrom on xuserusernamefrom.xuserid = fromxuserid
         join xuserusername as xuserusernameto on xuserusernameto.xuserid = toxuserid
         where (fromxuserid = $toOrFromXuserId OR toxuserid = $toOrFromXuserId) AND (sentat > $after)
         order by chatmessage.id, chatmessagesendervisibility.createdat desc,
         chatmessagereceivervisibility.createdat desc"""
      }

    query
    .map(OutgoingChatMessageWithVisibility.converter)
    .list()
    .apply()
    .toSeq.filter { message =>
      (message.fromId == toOrFromXuserId && message.senderVisibility == Visible) ||
      (message.toId == toOrFromXuserId && message.receiverVisibility == Visible) }
  }

}
