
package com.sgpt.mavenproject1.faculty;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.*;
import javafx.scene.control.Button;

public class FacultyArchiveController extends FacultyDashboardCentralController {

    @FXML
    private Button archiveBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        archiveBtn.getStyleClass().add("active");
    }
}
