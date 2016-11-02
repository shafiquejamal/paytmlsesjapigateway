package chat

import chat.ChatMessageVisibility.Visible
import chat.SocketMessageType.ToClientChat
import db.{CrauthAutoRollback, TestDBConnection, TestScalikeJDBCSessionProvider}
import org.scalatest.TryValues._
import org.scalatest._
import org.scalatest.fixture.FlatSpec
import scalikejdbc.DBSession
import user.UserFixture
import util.TestTimeProviderImpl

class ChatMessageFacadeATest
  extends FlatSpec
  with ShouldMatchers
  with CrauthAutoRollback
  with UserFixture
  with BeforeAndAfterEach
  with TestDBConnection {

  val timeProvider = new TestTimeProviderImpl()

  "Storing a message" should "be successful" in { session =>
    val api = makeAPI(session)
    val chatMessageWithVisibility = OutgoingChatMessageWithVisibility(
      ToClientChatMessage(ToClientChat, "alice", "bob", "some message", timeProvider.now().minusMillis(1).getMillis),
      Visible, Visible,
      id1,
      id3,
      uUIDProvider.randomUUID()
    )

    api.store(chatMessageWithVisibility).success.value shouldEqual chatMessageWithVisibility.toClientChatMessage
  }

  "Retrieving messages for a user" should "retrieve all messages that should be visible to the user" in { session =>
    val api = makeAPI(session)
    val expectedMessagesInvolvingBob = Seq(
      ToClientChatMessage(ToClientChat, "alice", "bob", "alice to bob one", dayBeforeYesterday.getMillis),
      ToClientChatMessage(ToClientChat, "alice", "bob", "alice to bob three", dayBeforeYesterday.getMillis),
      ToClientChatMessage(ToClientChat, "bob", "alice", "bob to alice one", dayBeforeYesterday.getMillis),
      ToClientChatMessage(ToClientChat, "bob", "alice", "bob to alice two", dayBeforeYesterday.getMillis)
    )

    api.messagesInvolving(id3) should contain theSameElementsAs expectedMessagesInvolvingBob
  }

  private def makeAPI(session: DBSession) = {
    val chatMessageDAO = new ChatMessageDAOImpl(TestScalikeJDBCSessionProvider(session), dBConfig)
    new ChatMessageFacade(chatMessageDAO, uUIDProvider, timeProvider)
  }
}
