
package com.sgpt.mavenproject1.faculty;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.*;
import javafx.scene.control.Button;

public class FacultyDashboardController extends FacultyDashboardCentralController {

    @FXML
    private Button homeBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        homeBtn.getStyleClass().add("active");
    }

    
}
