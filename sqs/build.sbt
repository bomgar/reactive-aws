name := "reactive-aws-sqs"

organization := "com.github.bomgar"

version := "1.0"

scalaVersion := "2.11.6"

scalacOptions += "-target:jvm-1.8"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "3.5",
  "org.specs2" %% "specs2-mock" % "3.5",
  "com.typesafe.play" %% "play-test" % "2.3.8"
).map(_ % "test")
