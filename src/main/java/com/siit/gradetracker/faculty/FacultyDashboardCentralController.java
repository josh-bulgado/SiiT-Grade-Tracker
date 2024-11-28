package com.siit.gradetracker.faculty;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.siit.gradetracker.SiiTApp;

import javafx.fxml.*;

public class FacultyDashboardCentralController implements Initializable {

    private static String department;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public static void setDepartment(String department) {
        FacultyDashboardCentralController.department = department;
    }

    @FXML
    private void backBtn() throws IOException {
        SiiTApp.setRoot("login");
    }

    @FXML
    private void toHome() throws IOException {
        SiiTApp.setRoot("faculty_dashboard");
    }

    @FXML
    private void toStudentList() throws IOException {
        SiiTApp.setRoot("faculty_student_list");
    }

    @FXML
    private void toArchive() throws IOException {
        SiiTApp.setRoot("faculty_archive");
    }
}
