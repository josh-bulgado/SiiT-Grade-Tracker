package com.siit.gradetracker.faculty;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import com.siit.gradetracker.*;;

public abstract class FacultyDashboardCentralController {

    @FXML
    protected Button dashboardButton, studentButton, archiveButton;

    protected static int programId;


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
    protected void toDashboard() throws IOException {
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
