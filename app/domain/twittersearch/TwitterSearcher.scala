package domain.twittersearch

import com.danielasfregola.twitter4s.TwitterRestClient
import org.joda.time.DateTime

import scala.concurrent.Future

object TwitterSearcher {

  def search(searchText: String): Future[Seq[TwitterSearchResult]] = {
    val client = TwitterRestClient()
    import scala.concurrent.ExecutionContext.Implicits.global
    client.searchTweet(searchText).map { ratedData =>
      ratedData.data.statuses.map { tweet =>
        TwitterSearchResult(
          tweet.id_str,
          tweet.user.fold("Unknown")(_.name),
          new DateTime(tweet.created_at.getTime),
          tweet.text)
      }
    }
  }

}
