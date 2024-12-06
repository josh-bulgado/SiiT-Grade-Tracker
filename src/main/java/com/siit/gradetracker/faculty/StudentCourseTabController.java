
package com.siit.gradetracker.faculty;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class StudentCourseTabController implements Initializable {

    private static String courseCode, courseDescription;
    private static int courseUnit;
    private static Double[] grades = new Double[4];

    @FXML
    private Text course_code, course_description, course_unit;

    @FXML
    private TextField prelim_grade, midterm_grade, prefinal_grade, final_grade;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateCourseInformation();
        updatePeriodGrade();
    }

    public static void setCourse(String courseCode, String courseDescription, int courseUnit, Double[] grades) {
        StudentCourseTabController.courseCode = courseCode;
        StudentCourseTabController.courseDescription = courseDescription;
        StudentCourseTabController.courseUnit = courseUnit;
        StudentCourseTabController.grades = grades;
    }

    private void updateCourseInformation() {
        course_code.setText(courseCode);
        course_description.setText(courseDescription);
        course_unit.setText(Integer.toString(courseUnit));
    }

   private void updatePeriodGrade() {
    prelim_grade.setText(doubleToString(grades[0]));
    midterm_grade.setText(doubleToString(grades[1]));
    prefinal_grade.setText(doubleToString(grades[2]));
    final_grade.setText(doubleToString(grades[3]));
}

private String doubleToString(Double value) {
    return value != null ? Double.toString(value) : "";
}

    // private String formatGrade(Double grade) {
    // return (grade == grade.intValue()) ? String.format("%d", grade.intValue()) :
    // String.format("%.2f", grade);
    // }

}
