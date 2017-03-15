package messaging

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.eigenroute.id.UUIDProvider
import com.eigenroute.time.TimeProvider
import domain.twittersearch.{API, SaveSearchTermMessage, SearchTermSavedMessage}
import entrypoint.UserAPI
import messaging.ClientPaths._

class ToServerMessageRouter(
    client: ActorRef,
    userAPI: UserAPI,
    api: API,
    clientId: UUID,
    clientUsername: String,
    timeProvider: TimeProvider,
    uUIDProvider: UUIDProvider)
  extends Actor
  with ActorLogging {

  override def receive = {

    case saveSearchTermMessage: SaveSearchTermMessage =>
      val result = api.addSearchTerm(clientId, saveSearchTermMessage.searchText)
      result.toOption.foreach { message =>
        val allAuthenticatorsForThisUser = context.actorSelection(namedClientPath(clientId))
        allAuthenticatorsForThisUser ! SearchTermSavedMessage(message)
      }

    case message =>
      log.info(s"Message for routing received $message")

  }

}

object ToServerMessageRouter {

  def props(
    client: ActorRef,
    userAPI: UserAPI,
    api: API,
    clientId: UUID,
    clientUsername: String,
    timeProvider: TimeProvider,
    uUIDProvider: UUIDProvider) =
      Props(
        new ToServerMessageRouter(
          client, userAPI, api, clientId, clientUsername, timeProvider, uUIDProvider))

}