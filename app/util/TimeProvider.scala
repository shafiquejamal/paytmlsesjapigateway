package util

import org.joda.time.DateTime

trait TimeProvider {

  def now(): DateTime

}
