package entrypoint

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class NamedClient(unnamedClient: ActorRef) extends Actor with ActorLogging {

  override def receive = {
    case msg: ToClientSocketMessage =>
      unnamedClient ! msg.toJson
  }

}

object NamedClient {
  def props(unnamedClient: ActorRef) = Props(new NamedClient(unnamedClient))
}