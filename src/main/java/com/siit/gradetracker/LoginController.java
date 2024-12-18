package com.siit.gradetracker;

import com.siit.gradetracker.faculty.FacultyDashboardCentralController;
import com.siit.gradetracker.main.*;
import com.siit.gradetracker.students.*;
import com.siit.gradetracker.util.DisplayError;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;

public class LoginController implements Initializable {

    private DisplayError de = new DisplayError();

    @FXML
    private TextField emailAddressField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordVisibleField;

    @FXML
    private Button togglePasswordBtn;

    @FXML
    private Label emailAddressErrorLabel, passwordErrorLabel;

    private boolean isPasswordVisible = false;

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
        passwordField.setOnKeyPressed(this::handleKeyPressed);
        passwordVisibleField.setOnKeyPressed(this::handleKeyPressed);

        emailAddressErrorLabel.setVisible(false);
        passwordErrorLabel.setVisible(false);
    }

    @FXML
    private void login() {
        emailAddressErrorLabel.setVisible(false);
        passwordErrorLabel.setVisible(false);
        CheckCredentials cc = new CheckCredentials();
        String emailAddress = emailAddressField.getText();
        String password = passwordField.getText();

        try {
            String role = cc.checkCredentials(emailAddress, password);
            if (role.equals("student")) {
                StudentDashboardController.setStudentId(fetchStudentId(emailAddress));
                System.out.println("invoke");
                SiiTApp.setRoot("student_dashboard");
            } else if (role.equals("faculty")) {
                FacultyDashboardCentralController.setProgramId(fetchProgramId(emailAddress));
                SiiTApp.setRoot("faculty_dashboard");
            } else {
                throw new IOException("Invalid email address or password");
            }

        } catch (CheckCredentials.EmailNotFoundException enfe) {
            emailAddressErrorLabel.setVisible(true);
            // de.showErrorDialog("Error", enfe.getMessage());
        } catch (CheckCredentials.IncorrectPasswordException ipe) {
            passwordErrorLabel.setVisible(true);
            // de.showErrorDialog("Error", ipe.getMessage());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            de.showErrorDialog("Error", ioe.getMessage());
        }

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

    private String fetchStudentId(String emailAddress) {

        try (Connection conn = DatabaseConnection.getConnection()) {

            String query = "SELECT si.student_id "
                    + "FROM auth.student_login sl "
                    + "JOIN students.student_information si ON sl.student_id = si.id "
                    + "WHERE sl.email_address = ?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, emailAddress);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("student_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int fetchProgramId(String emailAddress) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT al.program_id "
                    + "FROM auth.admin_login al "
                    + "WHERE al.email_address = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, emailAddress);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("program_id");
                    }
                }
            }

        } catch (SQLException e) {
            e.getStackTrace();
        }
        return 0;
    }

}
