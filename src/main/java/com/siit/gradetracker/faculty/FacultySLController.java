
package com.siit.gradetracker.faculty;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class FacultySLController extends FacultyBaseListController {

    @FXML
    private Button studentBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        studentBtn.getStyleClass().add("active");
    }
}
