package util

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.{FlatSpec, ShouldMatchers}
import play.api.Configuration

class PlayConfigParamsProviderUTest extends FlatSpec with ShouldMatchers {

  val underlying: Config = ConfigFactory.parseFile(new File("test/testfiles/conf/application.conf"))
  val configParamsProvider = new PlayConfigParamsProvider(new Configuration(underlying))

  "the config params" should "include all the params in the conf file" in {
    val configParams = configParamsProvider.configParams
    configParams.get("db.default.url") should contain("jdbc:h2:mem:play")
  }

}
