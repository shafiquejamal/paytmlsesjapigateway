package socket

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{FlatSpecLike, ShouldMatchers}
import util.StopSystemAfterAll

class NamedClientUTest
  extends TestKit(ActorSystem("testsystem"))
  with ShouldMatchers
  with FlatSpecLike
  with StopSystemAfterAll {

  val namedClient = system.actorOf(NamedClient.props(testActor))

  "The actor" should "send all messages to the unnamed client actor" in {

    val messageToSend = Vector[(Int, String)]((39, "some message to send"))

    namedClient ! messageToSend

    expectMsg(messageToSend)

  }

}

