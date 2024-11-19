package com.sgpt.mavenproject1;

import com.sgpt.mavenproject1.main.CheckCredentials;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

public class FacultyLoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void onFacultyDashboard() throws IOException {
        CheckCredentials cc = new CheckCredentials();
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Validate credentials
        if (cc.checkCredentials(username, password, true)) {
            App.setRoot("faculty_dashboard");

        } else {

            showErrorDialog("Invalid student ID or password");
        }
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void backBtn() throws IOException {
        App.setRoot("initial_page");
    }

}
