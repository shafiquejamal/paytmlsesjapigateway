package domain.twittersearch

import akka.stream.Materializer
import com.google.inject.Inject
import play.api.libs.ws.ahc.AhcWSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RandomWordFetcher @Inject() (materializer: Materializer) {

  implicit val implicitMaterializer = materializer

  def fetch: Future[String] = {
    val client = AhcWSClient()
    client
      .url("http://www.setgetgo.com/randomword/get.php")
      .withHeaders("Cache-Control" -> "no-cache")
      .get()
      .map { wsResponse => wsResponse.body }
      .andThen { case _ => client.close() }
  }

}
