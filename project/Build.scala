import sbt.Keys._
import sbt._

object Build extends Build {

  lazy val core = project in file("core")

  lazy val sqs = project.in(file("sqs")).dependsOn(core)

  lazy val root =
    Project(
      id = "reactive-aws",
      base = file(".")
    ).settings(
        name := "reactive-aws",
        organization := "com.github.bomgar",
        version := "1.0",
        scalaVersion := "2.11.6"
      ).aggregate(sqs, core)
}
