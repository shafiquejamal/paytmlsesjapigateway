package messaging

import access.authentication.ToServerLogoutMessage
import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class NamedClient(unnamedClient: ActorRef, authenticator: ActorRef)
  extends Actor
  with ActorLogging {

  override def receive = {

    case msg: ToClientSocketMessage =>
      unnamedClient ! msg.toJson

    case ToServerLogoutMessage =>
      authenticator ! ToServerLogoutMessage
  }

}

object NamedClient {

  def props(unnamedClient: ActorRef, authenticator: ActorRef) =
    Props(new NamedClient(unnamedClient, authenticator))

}