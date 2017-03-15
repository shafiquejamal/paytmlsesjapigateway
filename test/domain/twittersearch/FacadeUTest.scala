package domain.twittersearch

import domain.CommonFixture
import org.scalamock.scalatest.MockFactory
import org.scalatest.TryValues._
import org.scalatest.{FlatSpecLike, ShouldMatchers}

import scala.util.Success

class FacadeUTest extends FlatSpecLike with ShouldMatchers with MockFactory with CommonFixture {

  val mockedDAO = mock[DAO]

  "Adding a search term" should "create the search term and call the DAO's save method with the correct parameters" in {
    val facade = makeFacade
    val searchText = "some search text"
    val searchTerm = SearchTerm(id1, searchText, now)
    (mockedDAO.addSearchTerm _).expects(searchTerm).returning(Success(searchTerm))
    facade.addSearchTerm(id1, searchText).success.value shouldBe searchTerm
  }

  "Retrieving search terms for a user" should "call the DAO's search term method with the correct parameters" in {
    val facade = makeFacade
    val expected = Seq(
      SearchTerm(id1, "one", now),
      SearchTerm(id2, "two", now.plusMillis(1))
    )
    (mockedDAO.searchTerms _).expects(id1).returning(expected)
    facade.searchTerms(id1) should contain theSameElementsInOrderAs expected
  }

  def makeFacade = new Facade(uUIDProvider, mockedDAO, timeProvider)

}
