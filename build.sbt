ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.5"

val http4sVersion = "0.21.1"

lazy val root = (project in file("."))
  .settings(
    name := "Medallion"
  )

libraryDependencies ++= Dependencies.App
