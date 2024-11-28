package com.siit.gradetracker;

import javafx.fxml.FXML;
import java.io.IOException;

public class InitialPageController {

    @FXML
    private void onStudentLogin() throws IOException {
        SiiTApp.setRoot("student_login");
    }

    @FXML
    private void onFacultyLogin() throws IOException {
        SiiTApp.setRoot("faculty_login");
    }

}
