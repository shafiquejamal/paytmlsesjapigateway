package chat

import db.{TestScalikeJDBCSessionProvider, TestDBConnection, CrauthAutoRollback}
import org.scalatest._
import org.scalatest.fixture.FlatSpec
import scalikejdbc.DBSession
import user.UserFixture
import util.TestTimeProviderImpl
import org.scalatest.TryValues._

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
      OutgoingChatMessage("alice", "bob", "some message", timeProvider.now().minusMillis(1).getMillis),
      Both,
      id1,
      id3,
      uUIDProvider.randomUUID()
    )

    api.store(chatMessageWithVisibility).success.value shouldEqual chatMessageWithVisibility.outgoingChatMessage
  }

  "Retrieving messages for a user" should "retrieve all messages that should be visible to the user" in { session =>
    val api = makeAPI(session)
    val expectedMessagesInvolvingBob = Seq(
      OutgoingChatMessage("alice", "bob", "alice to bob one", dayBeforeYesterday.getMillis),
      OutgoingChatMessage("alice", "bob", "alice to bob three", dayBeforeYesterday.getMillis),
      OutgoingChatMessage("bob", "alice", "bob to alice one", dayBeforeYesterday.getMillis),
      OutgoingChatMessage("bob", "alice", "bob to alice two", dayBeforeYesterday.getMillis)
    )

    api.messagesInvolving(id3) should contain theSameElementsAs expectedMessagesInvolvingBob
  }

  private def makeAPI(session: DBSession) = {
    val chatMessageDAO = new ChatMessageDAOImpl(TestScalikeJDBCSessionProvider(session), dBConfig)
    new ChatMessageFacade(chatMessageDAO, uUIDProvider, timeProvider)
  }
}
