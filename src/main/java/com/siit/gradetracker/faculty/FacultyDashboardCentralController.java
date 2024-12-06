package com.siit.gradetracker.faculty;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.siit.gradetracker.SiiTApp;

import javafx.fxml.*;

public abstract class FacultyDashboardCentralController implements Initializable {

    protected static int programId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    protected abstract void fetchInformation();

    public static void setProgramId(int programId) {
        FacultyDashboardCentralController.programId = programId;
    }

    public int getProgramId() {
        return programId;
    }

    @FXML
    protected void backBtn() throws IOException {
        SiiTApp.setRoot("login");
    }

    @FXML
    protected void toHome() throws IOException {
        SiiTApp.setRoot("faculty_dashboard");
    }

    @FXML
    protected void toStudentList() throws IOException {
        SiiTApp.setRoot("faculty_student_list");
    }

    @FXML
    protected void toArchive() throws IOException {
        SiiTApp.setRoot("faculty_archive");
    }
}
