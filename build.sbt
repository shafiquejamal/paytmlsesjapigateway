name := """test-di"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  filters,
  "org.scalikejdbc" %% "scalikejdbc"       % "2.3.5",
  "com.h2database"  %  "h2"                % "1.4.191",
  "ch.qos.logback"  %  "logback-classic"   % "1.1.3",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % "test",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "net.codingwell" %% "scala-guice" % "4.0.1"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
