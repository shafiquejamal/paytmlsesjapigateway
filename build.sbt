name := """access-service"""

version := "0.0.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  filters,
  "com.eigenroute" % "eigenroute-publish-subscribe-rabbitmq-play_2.11" % "0.0.5",
  "com.eigenroute" % "eigenroute-messagebroker-messages_2.11" % "0.0.4",
  "com.eigenroute" % "eigenroute-scalikejdbc-helpers_2.11" % "0.0.1",
  "org.flywaydb" %% "flyway-play" % "3.0.0",
  "com.h2database"  %  "h2"                % "1.4.191",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11" % Test,
  "net.codingwell" %% "scala-guice" % "4.0.1",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.pauldijou" %% "jwt-play-json" % "0.9.2",
  "org.postgresql" % "postgresql" % "9.4.1208.jre7",
  "commons-validator" % "commons-validator" % "1.5.1",
  "com.typesafe.play" %% "play-mailer" % "5.0.0",
  "com.typesafe" % "config" % "1.3.0",
  "com.eigenroute" % "eigenroute-scalikejdbc-test-helpers_2.11" % "0.0.1" % Test,
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += "Eigenroute maven repo" at "http://mavenrepo.eigenroute.com/"


coverageEnabled in Test:= true

val testSettings = Seq(
  fork in Test := false
)

javaOptions in run += "-Dhttp.port=9001"
