package chat

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class TestActor(out: ActorRef) extends Actor with ActorLogging {

  override def receive = {
    case msg: String =>
      out ! s"${out.toString}: $msg"
      sender() ! s"${out.toString}: $msg"
  }

}

object TestActor {

  def props(client: ActorRef) = Props(new TestActor(client))

}