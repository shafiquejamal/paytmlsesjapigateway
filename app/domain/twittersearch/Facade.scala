package domain.twittersearch

import java.util.UUID

import akka.stream.Materializer
import com.eigenroute.id.UUIDProvider
import com.eigenroute.time.TimeProvider
import com.google.inject.Inject

import scala.concurrent.Future
import scala.util.Try

class Facade @Inject() (
    uUIDProvider: UUIDProvider,
    dAO: DAO,
    timeProvider: TimeProvider,
    materializer: Materializer)
  extends API {

  override def addSearchTerm(userId: UUID, searchText: String): Try[SearchTerm] =
    dAO addSearchTerm SearchTerm(userId, searchText, timeProvider.now())

  override def searchTerms(userId: UUID): Seq[SearchTerm] = dAO searchTerms userId

  override def search(searchText: String): Future[Seq[TwitterSearchResult]] = TwitterSearcher search searchText

  override def randomWord: Future[String] = new RandomWordFetcher(materializer).fetch
}
