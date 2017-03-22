package domain.twittersearch

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import org.scalatest.{FlatSpecLike, ShouldMatchers}

import scala.concurrent.Await
import scala.concurrent.duration._

class RandomWordFetcherUTest
  extends TestKit(ActorSystem("MySpec"))
  with ImplicitSender
  with FlatSpecLike
  with ShouldMatchers {

  implicit val materializer = ActorMaterializer()

  "Fetching a random word" should "yield a different word each time" in {
    val randomWordFetcher = new RandomWordFetcher(materializer)
    val maybeWord1 = randomWordFetcher.fetch
    val maybeWord2 = randomWordFetcher.fetch

    implicit val timeout = Timeout(10.seconds)
    val word1 = Await.result(maybeWord1, timeout.duration)
    val word2 = Await.result(maybeWord2, timeout.duration)

    word1 should not be empty
    word1 should not equal word2
  }

}
