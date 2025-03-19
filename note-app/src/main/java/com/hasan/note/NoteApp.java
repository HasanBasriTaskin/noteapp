package com.hasan.note;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for the Note App.
 * This class serves as the entry point for the JavaFX application.
 */
public class NoteApp extends Application {

  private static final Logger logger = LoggerFactory.getLogger(NoteApp.class);

  @Override
  public void start(Stage primaryStage) {
    try {
      logger.info("Starting Note App");

      // Load the main view
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
      Parent root = loader.load();

      // Set the scene
      Scene scene = new Scene(root);
      primaryStage.setScene(scene);
      primaryStage.setTitle("Note App");

      // Show the stage
      primaryStage.show();

      logger.info("Note App started successfully");
    } catch (Exception e) {
      logger.error("Error starting Note App", e);
    }
  }

  @Override
  public void stop() {
    // Clean up resources when the application is closing
    logger.info("Shutting down Note App");
  }

  /**
   * Main method to launch the application.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }
}