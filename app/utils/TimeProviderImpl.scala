package utils

import org.joda.time.DateTime

class TimeProviderImpl extends TimeProvider {

  override def now: DateTime = DateTime.now()

}
