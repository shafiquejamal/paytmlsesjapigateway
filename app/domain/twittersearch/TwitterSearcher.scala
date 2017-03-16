package domain.twittersearch

import com.danielasfregola.twitter4s.TwitterRestClient

import scala.concurrent.Future

object TwitterSearcher {

  def search(searchText: String): Future[Seq[String]] = {
    val client = TwitterRestClient()
    import scala.concurrent.ExecutionContext.Implicits.global
    client.searchTweet(searchText).map { ratedData =>
      ratedData.data.statuses.map(_.text)
    }
  }

}
