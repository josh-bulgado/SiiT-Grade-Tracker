package com.siit.gradetracker;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class SplashScreenController implements Initializable {

    @FXML
    private ProgressBar progressBar;

    public void initialize(URL url, ResourceBundle rb) {
        // Simulate a loading task using a background thread
        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Phase 1: Quickly move to 70% progress
                for (int i = 0; i <= 70; i++) {
                    Thread.sleep(10); // Very short delay to simulate quick loading
                    updateProgress(i, 100);
                }

                // Pause at 70% progress for 1 second
                Thread.sleep(1000); // 1 second pause

                // Phase 2: Quickly move to 100% progress after the pause
                for (int i = 71; i <= 100; i++) {
                    Thread.sleep(10); // Short delay to finish quickly
                    updateProgress(i, 100);
                }

                return null;
            }
        };

        // Bind the progress bar's progress to the task
        progressBar.progressProperty().bind(loadTask.progressProperty());

        // When the task is finished, close the splash screen and open the main app
        loadTask.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                // Close the splash screen
                Stage splashStage = (Stage) progressBar.getScene().getWindow();
                splashStage.close();

                // Launch the main application
                try {
                    new SiiTApp().start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        // Start the task in a background thread
        new Thread(loadTask).start();
    }
}
