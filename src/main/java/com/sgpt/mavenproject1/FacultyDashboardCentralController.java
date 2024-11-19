/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.sgpt.mavenproject1;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.*;

public class FacultyDashboardCentralController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void backBtn() throws IOException {
        App.setRoot("initial_page");
    }

    @FXML
    private void toHome() throws IOException {
        App.setRoot("faculty_dashboard");
    }

    @FXML
    private void toStudentList() throws IOException {
        App.setRoot("faculty_student_list");
    }

    @FXML
    private void toArchive() throws IOException {
        App.setRoot("faculty_archive");
    }
}
