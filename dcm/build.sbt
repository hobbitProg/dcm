import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.github.hobbitprog",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "dcm",
    parallelExecution in Test := false,
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.1",
    libraryDependencies += "org.tpolecat" %% "doobie-core" % "0.5.0",
    libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.144-R12",
    libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.13.5" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % Test,
    libraryDependencies += "org.hamcrest" % "hamcrest-core" % "1.3" % Test,
    libraryDependencies += "org.testfx" % "testfx-core" % "4.0.11-alpha" % Test,
    libraryDependencies += "org.eu.acolyte" %% "jdbc-scala" % "1.0.47" % Test,
    libraryDependencies += "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % Test
  )

