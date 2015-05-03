name := "reactive-aws-core"

organization := "com.github.bomgar"

version := "1.0"

scalaVersion := "2.11.6"

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
)

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "3.5",
  "org.specs2" %% "specs2-mock" % "3.5",
  "com.typesafe.play" %% "play-test" % "2.3.8"
).map(_ % "test")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-ws" % "2.3.8"
)


scalacOptions += "-target:jvm-1.8"
