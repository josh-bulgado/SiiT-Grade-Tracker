
package com.sgpt.mavenproject1;
    
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class FacultySLController extends FacultyDashboardCentralController {

    @FXML
    private Button studentBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        studentBtn.getStyleClass().add("active");
    }
}
