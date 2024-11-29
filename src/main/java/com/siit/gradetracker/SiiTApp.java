package com.siit.gradetracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SiiTApp extends Application {

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
        FXMLLoader fxmlLoader = new FXMLLoader(SiiTApp.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        SplashScreen.launchSplashScreen(args); // Start with the splash screen
    }

    public static class SplashScreen {

        public static void launchSplashScreen(String[] args) {
            Application.launch(SplashScreenApp.class, args); // Launch splash screen
        }

        public static class SplashScreenApp extends Application {

            @Override
            public void start(Stage splashStage) throws IOException {
                FXMLLoader loader = new FXMLLoader(SiiTApp.class.getResource("splash_screen.fxml"));
                Parent splashRoot = loader.load();
                Scene splashScene = new Scene(splashRoot, 750, 350);
                splashStage.setScene(splashScene);
                splashStage.setAlwaysOnTop(true);
                splashStage.setTitle("Loading...");
                splashStage.show();
            }
        }
    }

}