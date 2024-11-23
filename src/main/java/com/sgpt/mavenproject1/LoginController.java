package com.sgpt.mavenproject1;

import com.sgpt.mavenproject1.main.*;
import com.sgpt.mavenproject1.students.*;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.*;

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

                StudentDashboardController.setStudentId(fetchInformation(emailAddress));
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

    private String fetchInformation(String emailAddress) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();

            String query = "SELECT si.student_id "
                    + "FROM auth.student_login al "
                    + "JOIN students.student_information si ON al.student_id = si.id "
                    + "WHERE al.email_address = ?";

            stmt = conn.prepareStatement(query);
            stmt.setString(1, emailAddress);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("student_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
