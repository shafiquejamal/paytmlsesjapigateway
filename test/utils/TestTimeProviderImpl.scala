package utils

import org.joda.time.DateTime

object TestTimeProviderImpl extends TimeProvider {

  private var dateTime: DateTime = new DateTime(2016, 12, 30, 13, 14, 15)

  def setNow(newDateTime: DateTime): DateTime = {
    dateTime = newDateTime
    dateTime
  }

  override def now: DateTime = dateTime

}
