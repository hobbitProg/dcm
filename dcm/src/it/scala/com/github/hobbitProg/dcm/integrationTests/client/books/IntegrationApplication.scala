package com.github.hobbitProg.dcm.integrationTests.client.books

import javafx.application.Application

import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.stage.Stage

import com.github.hobbitProg.dcm.client.linuxDesktop.DCMDesktop

/**
  * Application for intgegration tests for distributed collection manager
  * @author Kyle Cranmer
  * @since 0.2
  */
class IntegrationApplication(
  private val desktop: DCMDesktop
) extends Application {

  /**
    * Start client application for distributed collection manager
    * @param primaryStage Primary stage of application
    */
  override def start(
    primaryStage: javafx.stage.Stage
  ): Unit = {
    val mainStage: Stage = primaryStage
    mainStage.scene =
      new Scene {
        root = desktop
      }
  }
}
