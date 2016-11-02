package chat

import chat.ChatMessageVisibility._
import chat.SocketMessageType.ChatMessage
import db.{CrauthAutoRollback, TestDBConnection, TestScalikeJDBCSessionProvider}
import org.scalatest._
import org.scalatest.fixture.FlatSpec
import scalikejdbc.DBSession
import user.UserFixture
import util.TestTimeProviderImpl

import scala.util.Success

class ChatMessageDAOUTest
  extends FlatSpec
  with ShouldMatchers
  with CrauthAutoRollback
  with UserFixture
  with BeforeAndAfterEach
  with TestDBConnection {

  val timeProvider = new TestTimeProviderImpl()

  "Adding a message" should "succeed" in { session =>
    val dAO = makeDAO(session)
    val chatMessageWithVisibility = OutgoingChatMessageWithVisibility(
      OutgoingChatMessage(ChatMessage, "alice", "bob", "some message", timeProvider.now().minusMillis(1).getMillis),
      Both,
      id1,
      id3,
      uUIDProvider.randomUUID()
    )
    val result = dAO.add(chatMessageWithVisibility, uUIDProvider.randomUUID(), timeProvider.now(), uUIDProvider.randomUUID())
    result shouldBe a[Success[_]]
  }

  "Adding message visibility" should "succeed" in { session =>
    val dAO = makeDAO(session)
    val result = dAO.addMessageVisibility(idMsgAliceBob3, timeProvider.now(), Neither, uUIDProvider.randomUUID())

    result shouldBe a[Success[_]]
  }

  "Retrieving messages for a user" should "return all messages that are visible to that user" in { session =>
    val dAO = makeDAO(session)

    val expectedMessages = Seq(
      OutgoingChatMessageWithVisibility(
        OutgoingChatMessage(ChatMessage, "alice", "bob", "alice to bob one", dayBeforeYesterday.getMillis), Both, id1, id3,
          idMsgAliceBob1),
      OutgoingChatMessageWithVisibility(
        OutgoingChatMessage(ChatMessage, "alice", "bob", "alice to bob two", dayBeforeYesterday.getMillis), SenderOnly, id1, id3,
          idMsgAliceBob2),
      OutgoingChatMessageWithVisibility(
        OutgoingChatMessage(ChatMessage, "bob", "alice", "bob to alice three", dayBeforeYesterday.getMillis), ReceiverOnly, id3, id1,
          idMsgBobAlice3)
    )

    dAO.visibleMessages(id1) should contain theSameElementsAs expectedMessages
  }

  private def makeDAO(session:DBSession) =
    new ChatMessageDAOImpl(TestScalikeJDBCSessionProvider(session), dBConfig)

}
