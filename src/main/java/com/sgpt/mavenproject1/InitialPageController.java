package com.sgpt.mavenproject1;

import javafx.fxml.FXML;
import java.io.IOException;

import com.sgpt.mavenproject1.main.App;

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
