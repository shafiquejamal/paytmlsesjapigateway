package domain.twittersearch

import java.util.UUID

import scala.util.Try

trait DAO {

  def addSearchTerm(searchTerm: SearchTerm): Try[SearchTerm]

  def searchTerms(userId: UUID): Seq[SearchTerm]

}
