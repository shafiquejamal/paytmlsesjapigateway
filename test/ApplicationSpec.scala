import org.scalatest.{ShouldMatchers, FlatSpec}
import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends FlatSpec with ShouldMatchers with OneAppPerTest {

  "Routes" should "send 404 on a bad request" in {
      route(app, FakeRequest(GET, "/boum")).map(status) should contain(NOT_FOUND)
  }

  "HomeController" should "render the index page" in {
      val home = route(app, FakeRequest(GET, "/")).get

      status(home) shouldBe OK
      contentType(home) should contain("text/plain")
  }

  "CountController" should "return an increasing count" in {
      contentAsString(route(app, FakeRequest(GET, "/count")).get) shouldBe "0"
      contentAsString(route(app, FakeRequest(GET, "/count")).get) shouldBe "1"
      contentAsString(route(app, FakeRequest(GET, "/count")).get) shouldBe "2"
  }

}
