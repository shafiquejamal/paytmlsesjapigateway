package socket

import akka.actor.{Props, Actor, ActorLogging, ActorRef}

class NamedClient(unnamedClient: ActorRef) extends Actor with ActorLogging {

  override def receive = {

    case msg =>
      unnamedClient ! msg

  }

}

object NamedClient {
  def props(unnamedClient: ActorRef) = Props(new NamedClient(unnamedClient))
}