package util

import org.joda.time.DateTime

class TimeProviderImpl extends TimeProvider {

  override def now(): DateTime = DateTime.now()

}

object TimeProviderImpl {
  def apply() = new TimeProviderImpl()
}
