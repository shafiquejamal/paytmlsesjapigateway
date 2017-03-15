package domain.twittersearch

import com.eigenroute.scalikejdbctesthelpers.{CrauthAutoRollback, TestDBConnection, TestScalikeJDBCSessionProvider}
import org.scalatest.TryValues._
import org.scalatest._
import org.scalatest.fixture.FlatSpec
import scalikejdbc.DBSession

import scala.util.Failure

class ScalikeJDBCDAOImplUTest
  extends FlatSpec
  with ShouldMatchers
  with Fixture
  with CrauthAutoRollback
  with BeforeAndAfterEach
  with TestDBConnection {

  "Adding a search term to the db" should "succeed if the user is valid, and fail otherwise" in { session =>
    val dAO = makeDAO(session)
    val userId = id1
    val searchTermToAdd = SearchTerm(userId, "some search term", timeProvider.now())
    dAO.addSearchTerm(searchTermToAdd).success.value shouldBe searchTermToAdd
    dAO.addSearchTerm(searchTermToAdd.copy(userId = uUIDProvider.randomUUID())) shouldBe a[Failure[_]]
  }

  "Retrieving search terms" should "return only the search terms stored for that user" in { session =>
    val dAO = makeDAO(session)
    val retrievedSearches = dAO.searchTerms(id1)
    val expected = Seq(
      SearchTerm(id1, "second search text user 1", now),
      SearchTerm(id1, "first search text user 1", yesterday)
    )
    retrievedSearches should contain theSameElementsInOrderAs expected
  }

  private def makeDAO(session: DBSession) =
    new ScalikeJDBCDAOImpl(uUIDProvider, new TestScalikeJDBCSessionProvider(session), dBConfig)

}
