import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.github.hobbitProg",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "dcm",
    parallelExecution in Test := false,
    scalacOptions += "-feature",
    libraryDependencies += "org.tpolecat" %% "doobie-core-cats" % "0.4.4",
    libraryDependencies += "org.typelevel" % "cats-core_2.12" % "0.9.0",
    libraryDependencies += "org.testfx" % "testfx-core" % "4.0.8-alpha" % "test",
    libraryDependencies += "org.wildfly.swarm" % "javafx" % "2017.11.0",
    libraryDependencies += "org.scalafx" % "scalafx_2.12" % "8.0.144-R12",
    libraryDependencies += "org.specs2" % "specs2-core_2.12" % "4.0.0-RC4" % "test",
    libraryDependencies += "org.specs2" % "specs2-scalacheck_2.12" % "4.0.0-RC4" % "test",
    libraryDependencies += "org.eu.acolyte" % "jdbc-scala_2.12" % "1.0.46" % "test",
    libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.20.0" % "test",
    libraryDependencies += "org.loadui" % "testFx" % "3.1.2" % "test",
    libraryDependencies += "org.scalamock" % "scalamock-specs2-support_2.12" % "3.6.0" % "test",
    libraryDependencies += "org.scalatest" % "scalatest_2.12" % "3.2.0-SNAP9" % "test",
    libraryDependencies += "org.scalamock" % "scalamock-scalatest-support_2.12" % "3.6.0" % "test"

  )

val printTests = taskKey[Unit]("something")

printTests := {
  val tests = (definedTests in Test).value
   tests map { t =>
    println(t.name)
  }
}
