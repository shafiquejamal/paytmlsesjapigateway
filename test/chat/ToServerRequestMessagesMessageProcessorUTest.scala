package chat

import akka.actor.ActorSystem
import akka.testkit.TestKit
import contact.ToServerRequestContactsMessage
import org.joda.time.DateTime
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpecLike, ShouldMatchers}
import util.{StopSystemAfterAll, TestUUIDProviderImpl}

class ToServerRequestMessagesMessageProcessorUTest
  extends TestKit(ActorSystem("testsystem"))
  with ShouldMatchers
  with FlatSpecLike
  with StopSystemAfterAll
  with MockFactory {

  val mockChatMessageAPI = mock[ChatMessageAPI]
  val uUIDProvider = new TestUUIDProviderImpl()
  val clientId = uUIDProvider.randomUUID()
  val toServerRequestMessagesMessageProcessor =
    system.actorOf(ToServerRequestMessagesMessageProcessor.props(testActor, mockChatMessageAPI, clientId))

  "The ToServerRequestMessagesMessageProcessor" should "send to the client messages created after the specified date" in {
    val toServerRequestMessagesMessage = ToServerRequestMessagesMessage(Some(new DateTime(2010, 1, 1, 0, 0, 0)))
    val messages =
      Seq(
        ToClientChatMessage(
          Chat(uUIDProvider.randomUUID(), "from1", "to1", "text1", new DateTime(2010, 1, 1, 0, 0, 0).getMillis)),
        ToClientChatMessage(
          Chat(uUIDProvider.randomUUID(), "from2", "to2", "text2", new DateTime(2010, 1, 2, 0, 0, 0).getMillis))
      )

    (mockChatMessageAPI.messagesInvolving _).expects(clientId, toServerRequestMessagesMessage.maybeSince).returning(messages)

    toServerRequestMessagesMessageProcessor ! toServerRequestMessagesMessage

    expectMsg(ToClientMessagesSinceMessage(messages))
  }

  it should "send no message to the client if the message is not of type ToServerRequestMessagesMessage" in {
    val toServerRequestMessagesMessageProcessor =
      system.actorOf(ToServerRequestMessagesMessageProcessor.props(testActor, mockChatMessageAPI, clientId))

    toServerRequestMessagesMessageProcessor ! ToServerRequestContactsMessage("")

    expectNoMsg()
  }

}
