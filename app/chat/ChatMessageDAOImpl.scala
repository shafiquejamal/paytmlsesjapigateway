package chat

import java.util.UUID

import chat.ChatMessageVisibility.{Both, ReceiverOnly, SenderOnly}
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

    val maybeVisibilityAdded = addVisibility(chatMessageUUID, createdAt, chatMessage.visibility, visibilityUUID)

    if (insertedMessages == 1 && maybeVisibilityAdded.isSuccess)
      Success(chatMessage)
    else
      Failure(new Exception("Failed to add message or visibility"))
  }

  override def addMessageVisibility(
    chatMessageUUID: UUID,
    createdAt: DateTime,
    visibility: ChatMessageVisibility,
    visibilityUUID: UUID): Try[UUID] = {

    implicit val session = scalikeJDBCSessionProvider.provideAutoSession
    addVisibility(chatMessageUUID, createdAt, visibility, visibilityUUID)
  }

  def addVisibility(
      chatMessageUUID: UUID,
      createdAt: DateTime,
      visibility: ChatMessageVisibility,
      visibilityUUID: UUID)(implicit session:DBSession): Try[UUID] = {

    val insertedVisibility = sql"""insert into chatmessagevisibility (id, chatmessageid, visibility, createdat) VALUES
      (${visibilityUUID}, ${chatMessageUUID}, ${visibility.number}, $createdAt)""".update().apply()
    if (insertedVisibility == 1)
      Success(visibilityUUID)
    else
      Failure(new Exception("Unable to add visibility"))
  }

  override def visibleMessages(toOrFromXuserId: UUID): Seq[OutgoingChatMessageWithVisibility] = {
    implicit val readOnlySession = scalikeJDBCSessionProvider.provideReadOnlySession
    sql"""select DISTINCT ON (chatmessage.id) fromxuserid, toxuserid, messagetext, sentat, visibility, chatmessageid,
          xuserusernamefrom.username as fromusername, xuserusernameto.username as tousername from chatmessage
         join chatmessagevisibility on chatmessagevisibility.chatmessageid = chatmessage.id
         join xuserusername as xuserusernamefrom on xuserusernamefrom.xuserid = fromxuserid
         join xuserusername as xuserusernameto on xuserusernameto.xuserid = toxuserid
         where fromxuserid = $toOrFromXuserId OR toxuserid = $toOrFromXuserId
         order by chatmessage.id, chatmessagevisibility.createdat desc"""
    .map(OutgoingChatMessageWithVisibility.converter).list().apply().toSeq.filter{ message =>
      message.visibility == Both ||
      (message.fromId == toOrFromXuserId && message.visibility == SenderOnly) ||
      (message.toId == toOrFromXuserId && message.visibility == ReceiverOnly) }
  }

}
