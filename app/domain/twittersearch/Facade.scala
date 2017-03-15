package domain.twittersearch

import java.util.UUID

import com.eigenroute.id.UUIDProvider
import com.eigenroute.time.TimeProvider
import com.google.inject.Inject

import scala.util.Try

class Facade @Inject() (
    uUIDProvider: UUIDProvider,
    dAO: DAO,
    timeProvider: TimeProvider)
  extends API {

  def addSearchTerm(userId: UUID, searchText: String): Try[SearchTerm] =
    dAO.addSearchTerm(SearchTerm(userId, searchText, timeProvider.now()))

  def searchTerms(userId: UUID): Seq[SearchTerm] = dAO.searchTerms(userId)

}
