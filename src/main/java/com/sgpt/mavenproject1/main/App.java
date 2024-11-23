package com.sgpt.mavenproject1.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("login"));
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        SplashScreen.launchSplashScreen(args); // Start with the splash screen
    }

    /**
     * Inner class to manage splash screen functionality
     */
    public static class SplashScreen {

        public static void launchSplashScreen(String[] args) {
            Application.launch(SplashScreenApp.class, args);
        }

        public static class SplashScreenApp extends Application {

            @Override
            public void start(Stage splashStage) {
                // Create splash screen content
                StackPane root = new StackPane();
                ImageView splashImage = new ImageView(
                        new Image(App.class.getResource("/images/Bratz2021Apparel.png").toExternalForm()));

                splashImage.setFitWidth(960);
                splashImage.setPreserveRatio(true);
                root.getChildren().add(splashImage);

                Scene splashScene = new Scene(root, 960, 250); // Adjust dimensions
                splashStage.setScene(splashScene);
                splashStage.setAlwaysOnTop(true);
                splashStage.setTitle("Loading...");
                splashStage.show();

                // Simulate loading task
                Task<Void> loadTask = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(500); // Simulate loading delay
                        return null;
                    }
                };

                loadTask.setOnSucceeded(event -> {
                    splashStage.close(); // Close splash screen
                    Platform.runLater(() -> {
                        // Launch the main application
                        try {
                            new App().start(new Stage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                });

                new Thread(loadTask).start();
            }
        }
    }

}