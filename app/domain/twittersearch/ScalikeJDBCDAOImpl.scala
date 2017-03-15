package domain.twittersearch

import java.util.UUID

import com.eigenroute.id.UUIDProvider
import com.eigenroute.scalikejdbchelpers.{DBConfig, ScalikeJDBCSessionProvider}
import com.google.inject.Inject
import scalikejdbc._

import scala.util.{Failure, Success, Try}

class ScalikeJDBCDAOImpl @Inject() (
    uUIDProvider: UUIDProvider,
    scalikeJDBCSessionProvider: ScalikeJDBCSessionProvider,
    dBConfig: DBConfig)
  extends DAO {

    def addSearchTerm(searchTerm: SearchTerm): Try[SearchTerm] = {
      implicit val session = scalikeJDBCSessionProvider.provideAutoSession
      val id = uUIDProvider.randomUUID()
      val query =
        Try(sql"""INSERT INTO searchterm (id, xuserid, searchText, createdat) values (
             $id, ${searchTerm.userId}, ${searchTerm.searchText}, ${searchTerm.createdAt}
             )""".update().apply()).getOrElse(0)
      if (query == 1) {
        Success(searchTerm)
      } else {
        Failure(new Exception("Could not add search term to db"))
      }
    }

    def searchTerms(userId: UUID): Seq[SearchTerm] = {
      implicit val session = scalikeJDBCSessionProvider.provideReadOnlySession
      sql"""SELECT xuserid, searchtext, createdat FROM searchterm WHERE xuserid = $userId ORDER BY createdat DESC"""
        .map( rs => SearchTerm(userId, rs.string("searchtext"), rs.jodaDateTime("createdat")))
        .list().apply()
    }

}
