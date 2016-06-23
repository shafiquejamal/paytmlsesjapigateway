package utils

import org.joda.time.DateTime

trait TimeProvider {

  def now: DateTime

}
