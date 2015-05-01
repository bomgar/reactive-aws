name := "reactive-aws-core"

organization := "com.github.bomgar"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "3.5"
).map(_ % "test")
