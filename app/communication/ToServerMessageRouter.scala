package communication

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.eigenroute.id.UUIDProvider
import com.eigenroute.time.TimeProvider
import entrypoint.UserAPI

class ToServerMessageRouter(
    client: ActorRef,
    userAPI: UserAPI,
    clientId: UUID,
    clientUsername: String,
    timeProvider: TimeProvider,
    uUIDProvider: UUIDProvider)
  extends Actor
  with ActorLogging {

  override def receive = {

    case message =>
      log.info("Message for routing received", message)

  }

}

object ToServerMessageRouter {

  def props(
    client: ActorRef,
    userAPI: UserAPI,
    clientId: UUID,
    clientUsername: String,
    timeProvider: TimeProvider,
    uUIDProvider: UUIDProvider) =
      Props(
        new ToServerMessageRouter(
          client, userAPI, clientId, clientUsername, timeProvider, uUIDProvider))

}