package db

import org.scalatest._
import play.api.Application
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceableModule}
import play.api.test._

trait OneAppPerTestWithOverrides extends SuiteMixin { this: Suite ⇒

  def overrideModules: Seq[GuiceableModule] = Nil

  def newAppForTest(testData: TestData): Application =
    new GuiceApplicationBuilder()
    .overrides(overrideModules: _*)
    .build

  var appPerTest: Application = _

  implicit final def app: Application = synchronized { appPerTest }

  abstract override def withFixture(test: NoArgTest) = {
    synchronized { appPerTest = newAppForTest(test) }
    Helpers.running(app) { super.withFixture(test) }
  }
}