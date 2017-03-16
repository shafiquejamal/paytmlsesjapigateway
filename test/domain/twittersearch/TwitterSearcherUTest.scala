package domain.twittersearch

import akka.util.Timeout
import org.scalatest.{FlatSpecLike, ShouldMatchers}

import scala.concurrent.Await
import scala.concurrent.duration._

class TwitterSearcherUTest extends FlatSpecLike with ShouldMatchers {

  "The twitter searcher" should "return results from twitter" in {

    val searchResults = TwitterSearcher.search("cats")
    implicit val timeout = Timeout(5.seconds)
    val results = Await.result(searchResults, timeout.duration)
    results should not be empty

  }

}
