package com.sgpt.mavenproject1;

import com.sgpt.mavenproject1.main.CheckCredentials;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class StudentLoginController implements Initializable {

    @FXML
    private TextField studentIdField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        studentIdField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
        });

        passwordField.setOnKeyPressed(this::handleKeyPressed);
    }

    @FXML
    private void onStudentLogin() {
        CheckCredentials cc = new CheckCredentials();
        String studentId = studentIdField.getText();
        String password = passwordField.getText();

        try {
            if (cc.checkCredentials(studentId, password, false)) {

                StudentDashboardController.setStudentId(studentId);
                App.setRoot("student_dashboard");

            }
        } catch (IOException ioe) {

            // If credentials are invalid, show an error message
            showErrorDialog("Invalid student ID or password");
        }
        // Validate credentials

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

    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onStudentLogin();
        }
    }

}
