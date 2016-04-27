package fetcher

import org.scalatest.{ShouldMatchers, FlatSpec}

class FetcherUTest extends FlatSpec with ShouldMatchers with Fetcher[TestActive] {

  "filtering out inactive objects" should "filter out objects whose active property is false" in {
    val inActive = TestActive(isActive = false)
    keepIfActive(Some(inActive)) shouldBe empty
  }

  it should "retain objects whose active property is true" in {
    val active = TestActive(isActive = true)
    keepIfActive(Some(active)) should contain(active)
  }
}
