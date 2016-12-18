    import com.typesafe.sbt.SbtMultiJvm
    import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

    val akkaVersion = "2.4.11"

    val project = Project(
        id = "dcm",
        base = file(".")
      )
      .settings(SbtMultiJvm.multiJvmSettings: _*)
      .settings(
        name := "dcm",
        version := "0.1",
        scalaVersion := "2.11.8",
        unmanagedBase := baseDirectory.value / "lib",
        libraryDependencies ++= Seq(
          "com.typesafe.akka" %% "akka-actor" % akkaVersion,
          "com.typesafe.akka" %% "akka-remote" % akkaVersion,
          "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion,
          "org.scala-lang" % "scala-reflect" % "2.11.8",
          "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.4",
          "org.jbehave" % "jbehave-core" % "4.0.5",
          "org.jbehave" % "jbehave-scala" % "4.0.5",
          "org.scalatest" %% "scalatest" % "2.2.1" % "test",
          "org.eu.acolyte" % "jdbc-scala_2.11" % "1.0.42-j7p",
          "org.scalamock" % "scalamock-scalatest-support_2.11" % "3.3.0",
          "org.testfx" % "testfx-junit" % "4.0.4-alpha",
          "org.scalafx" % "scalafx_2.11" % "8.0.102-R11",
          "com.novocode" % "junit-interface" % "0.11"),
        // make sure that MultiJvm test are compiled by the default test compilation
        compile in MultiJvm <<= (compile in MultiJvm) triggeredBy (compile in Test),
        // disable parallel tests
        parallelExecution in Test := false,
        // Generate xml test results
        testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-u",
          "target/test-reports"),
        // make sure that MultiJvm tests are executed by the default test target,
        // and combine the results from ordinary test and multi-jvm tests
        executeTests in Test <<= (executeTests in Test, executeTests in MultiJvm) map {
          case (testResults, multiNodeResults)  =>
            val overall =
              if (testResults.overall.id < multiNodeResults.overall.id)
                multiNodeResults.overall
              else
                testResults.overall
            Tests.Output(overall,
              testResults.events ++ multiNodeResults.events,
              testResults.summaries ++ multiNodeResults.summaries)
        }
      )
      .configs (MultiJvm)

