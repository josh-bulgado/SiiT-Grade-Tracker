
package com.siit.gradetracker.faculty;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.*;
import javafx.scene.control.Button;

public class FacultyArchiveController extends FacultyBaseListController {

    @FXML
    private Button archiveBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        archiveBtn.getStyleClass().add("active");
    }
}
