import Dependencies._

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    inThisBuild(List(
      organization := "com.github.hobbitProg",
      scalaVersion :=  "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "dcm",
    Defaults.itSettings,
    parallelExecution in Test := false,
    parallelExecution in IntegrationTest:= false,
    scalacOptions += "-feature",
    scalacOptions += "-Ypartial-unification",
    scalacOptions += "-deprecation",
    libraryDependencies += "org.tpolecat" %% "doobie-core" % "0.5.0-M10",
    libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.0-RC1",
    libraryDependencies += "org.testfx" % "testfx-core" % "4.0.8-alpha" % "test,it",
    libraryDependencies += "org.wildfly.swarm" % "javafx" % "2017.11.0",
    libraryDependencies += "org.scalafx" % "scalafx_2.12" % "8.0.144-R12",
    libraryDependencies += "org.specs2" % "specs2-core_2.12" % "4.0.0-RC4" % "test",
    libraryDependencies += "org.specs2" % "specs2-scalacheck_2.12" % "4.0.0-RC4" % "test",
    libraryDependencies += "org.eu.acolyte" % "jdbc-scala_2.12" % "1.0.46" % "test",
    libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.20.0" % "test",
    libraryDependencies += "org.loadui" % "testFx" % "3.1.2" % "test",
    libraryDependencies += "org.scalamock" % "scalamock-specs2-support_2.12" % "3.6.0" % "test",
    libraryDependencies += "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % "test,it",
    libraryDependencies += "org.scalatest" % "scalatest_2.12" % "3.2.0-SNAP9" % "test,it",
    libraryDependencies += "org.scalamock" % "scalamock-scalatest-support_2.12" % "3.6.0" % "test",
    libraryDependencies += "co.fs2" %% "fs2-core" % "0.10.0-M9",
    libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.2",
    libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.21.0.1"
)

val printTests = taskKey[Unit]("something")

printTests := {
  val tests = (definedTests in Test).value
   tests map { t =>
    println(t.name)
  }
}
