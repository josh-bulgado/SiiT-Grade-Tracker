package com.sgpt.mavenproject1;

import javafx.fxml.FXML;
import java.io.IOException;

public class InitialPageController {

    @FXML
    private void onStudentLogin() throws IOException {
        App.setRoot("student_login");
    }

    @FXML
    private void onFacultyLogin() throws IOException {
        App.setRoot("faculty_login");
    }

}
