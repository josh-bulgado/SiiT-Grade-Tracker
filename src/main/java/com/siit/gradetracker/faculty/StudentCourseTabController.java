
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
    private static double[] grades = new double[4];

    @FXML
    private Text course_code, course_description, course_unit;

    @FXML
    private TextField prelim_grade, midterm_grade, prefinal_grade, final_grade;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateCourseInformation();
        updateCourseGrade();
    }

    public static void setCourse(String courseCode, String courseDescription, int courseUnit, double[] grades) {
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

    // private void updateCourseGrade() {
    // prelim_grade.setText(grades[0]);
    // midterm_grade.setText(grades[1] >= 0.0 ? formatGrade(grades[1]) : "0.0");
    // prefinal_grade.setText(grades[2] >= 0.0 ? formatGrade(grades[2]) : "0.0");
    // final_grade.setText(grades[3] >= 0.0 ? formatGrade(grades[3]) : "0.0");
    // }
    private void updateCourseGrade() {
        prelim_grade.setText(Double.toString(grades[0]));
        midterm_grade.setText(Double.toString(grades[1]));
        prefinal_grade.setText(Double.toString(grades[2]));
        final_grade.setText(Double.toString(grades[3]));
    }

    // private String formatGrade(Double grade) {
    // return (grade == grade.intValue()) ? String.format("%d", grade.intValue()) :
    // String.format("%.2f", grade);
    // }

}
