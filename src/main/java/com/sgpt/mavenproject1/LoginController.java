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
import javafx.scene.shape.SVGPath;

public class LoginController implements Initializable {

    @FXML
    private TextField emailAddressField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordVisibleField;

    @FXML
    private Button togglePasswordBtn;

    private boolean isPasswordVisible = false;

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        passwordField.setOnKeyPressed(this::handleKeyPressed);
        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    @FXML
    private void login() {
        CheckCredentials cc = new CheckCredentials();
        String emailAddress = emailAddressField.getText();
        String password = passwordField.getText();

        try {
            String role = cc.checkCredentials(emailAddress, password);
            if (role.equals("student")) {
                StudentDashboardController.setStudentId(2000 + emailAddress.replaceAll("\\D", ""));
                App.setRoot("student_dashboard");
            }

            if (role.equals("faculty")) {
                FacultyDashboardCentralController.setDepartment(role);
                App.setRoot("faculty_dashboard");
            }
        } catch (IOException ioe) {
            // If credentials are invalid, show an error message
            showErrorDialog("Invalid email address or password");
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
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            // Show the plain text field and hide the PasswordField
            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            // Show the PasswordField and hide the plain text field
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);
        }
    }

    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            login();
        }
    }

}
