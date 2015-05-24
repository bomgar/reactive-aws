import sbt.Keys._
import sbt._

object Build extends Build {

  val specs2Version = "3.6"
  val playVersion = "2.3.9"

  val testDependencies = Seq(
    "org.specs2" %% "specs2-core" % specs2Version,
    "org.specs2" %% "specs2-mock" % specs2Version,
    "com.typesafe.play" %% "play-test" % playVersion
  ).map(_ % "test")

  val libDependencies = Seq(
    "com.typesafe.play" %% "play-ws" % playVersion
  )


  val commonSettings = Seq(
    version := "1.0",
    organization := "com.github.bomgar",
    scalaVersion := "2.11.6",
    scalacOptions := Seq("-target:jvm-1.8"),
    testOptions in Test := Seq(Tests.Argument("exclude", "integration")),
    resolvers ++= Seq(
      "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
    ),
    libraryDependencies := libDependencies ++ testDependencies,
    ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }
  )

  lazy val core = (project in file("core"))
    .settings(commonSettings)
    .settings(name := "reactive-aws-core")

  lazy val sqs = project.in(file("sqs"))
    .dependsOn(core)
    .settings(commonSettings)
    .settings(name := "reactive-aws-sqs")

  lazy val sns = project.in(file("sns"))
    .dependsOn(core)
    .settings(commonSettings)
    .settings(name := "reactive-aws-sns")

  lazy val root =
    Project(
      id = "reactive-aws",
      base = file("."),
      settings = commonSettings
    )
      .settings(
        name := "reactive-aws"
      )
      .aggregate(sqs, sns, core)
}
