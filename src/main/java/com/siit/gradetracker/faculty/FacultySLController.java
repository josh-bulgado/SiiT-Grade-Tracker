
package com.siit.gradetracker.faculty;

import java.net.URL;
import java.util.ResourceBundle;

public class FacultySLController extends FacultyBaseListController {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        studentButton.getStyleClass().add("active");
        super.initialize(url, rb);
    }
}
