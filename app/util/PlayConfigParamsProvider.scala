package util

import com.google.inject.Inject
import play.api.Configuration

class PlayConfigParamsProvider @Inject()(configuration: Configuration) extends ConfigParamsProvider {

  override def configParams: Map[String, String] = {
    configuration.entrySet.map { case (key, value) => key -> value.render().replace(""""""", "") }.toMap
  }

}
