
package com.siit.gradetracker.faculty;

import java.net.URL;
import java.util.ResourceBundle;

public class FacultyArchiveController extends FacultyBaseListController {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        archiveButton.getStyleClass().add("active");
        super.initialize(url, rb);
    }
}
