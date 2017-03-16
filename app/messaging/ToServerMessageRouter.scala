package messaging

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.eigenroute.id.UUIDProvider
import com.eigenroute.time.TimeProvider
import domain.twittersearch._
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

    case SaveSearchTermMessage(searchText) =>
      val result = api.addSearchTerm(clientId, searchText)
      result.toOption.foreach { searchTerm =>
        val allAuthenticatorsForThisUser = context.actorSelection(namedClientPath(clientId))
        allAuthenticatorsForThisUser ! SearchTermSavedMessage(searchTerm)
      }

    case RetrieveSearchTermsMessage =>
      val savedSearchTerms = api.searchTerms(clientId)
      val allAuthenticatorsForThisUser = context.actorSelection(namedClientPath(clientId))
      savedSearchTerms.foreach { savedSearchTerm =>
        allAuthenticatorsForThisUser ! SearchTermSavedMessage(savedSearchTerm)
      }

    case SearchTwitterMessage(searchText) =>
      import context.dispatcher
      api.search(searchText) onSuccess {
        case searchResults: List[String] =>
          client ! TwitterSearchResultsMessage(searchResults)
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