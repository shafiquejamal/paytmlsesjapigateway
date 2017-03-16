package domain.twittersearch

import java.util.UUID

import scala.concurrent.Future
import scala.util.Try

trait API {

  def addSearchTerm(userId: UUID, searchText: String): Try[SearchTerm]

  def searchTerms(userId: UUID): Seq[SearchTerm]

  def search(searchText: String): Future[Seq[String]]

}
