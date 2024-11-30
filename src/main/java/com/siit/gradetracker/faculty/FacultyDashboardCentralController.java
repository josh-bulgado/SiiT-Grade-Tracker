package com.siit.gradetracker.faculty;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.siit.gradetracker.SiiTApp;

import javafx.fxml.*;

public class FacultyDashboardCentralController implements Initializable {

    protected static int programId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public static void setProgramId(int programId) {
        FacultyDashboardCentralController.programId = programId;
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
