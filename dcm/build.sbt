import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.github.hobbitProg",
      scalaVersion := "2.12.3",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "dcm",
    libraryDependencies += "org.typelevel" % "cats-core_2.12" % "1.0.0-MF",
    libraryDependencies += "org.specs2" % "specs2-core_2.12" % "4.0.0-RC4" % "test",
    libraryDependencies += "org.specs2" % "specs2-scalacheck_2.12" % "4.0.0-RC4" % "test"
  )
