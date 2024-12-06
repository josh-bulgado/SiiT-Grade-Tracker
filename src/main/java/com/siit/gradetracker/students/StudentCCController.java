
package com.siit.gradetracker.students;

import java.net.URL;
import java.util.ResourceBundle;

import com.siit.gradetracker.main.Course;

import javafx.fxml.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class StudentCCController implements Initializable {

    private Course course;
    private String courseDescription;
    private Double[] grades = new Double[4];
    private double courseGrade;

    @FXML
    private Text courseDescriptionText;

    @FXML
    private Text prelimGradeText, midtermGradeText, preFinalGradeText, finalGradeText;

    @FXML
    private Text courseGradeText;

    @FXML
    private GridPane courseGradeGridPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setCourse(Course course) {
        this.course = course;
        updateUI();
    }

    @FXML
    private void removeCourseGradeGridPane() {

        BorderPane borderPane = (BorderPane) courseGradeGridPane.getParent();

        if (borderPane != null) {
            borderPane.setBottom(null);
        }
    }

    private void updateUI() {
        courseDescriptionText.setText(course.getCourseDescription());
        updateGradesDisplay();
        if (isAnyGradeZero()) {
            removeCourseGradeGridPane();
        }
    }

    private void updateGradesDisplay() {
        prelimGradeText.setText(course.getGrades()[0] != 0 ? formatGrade(course.getGrades()[0]) : "");
        midtermGradeText.setText(course.getGrades()[1] != 0 ? formatGrade(course.getGrades()[1]) : "");
        preFinalGradeText.setText(course.getGrades()[2] != 0 ? formatGrade(course.getGrades()[2]) : "");
        finalGradeText.setText(course.getGrades()[3] != 0 ? formatGrade(course.getGrades()[3]) : "");
        courseGradeText.setText(Double.toString(course.getCourseGrade()));
    }

    private String formatGrade(Double grade) {
        return (grade == grade.intValue()) ? String.format("%d", grade.intValue()) : String.format("%.2f", grade);
    }

    private boolean isAnyGradeZero() {
        this.grades = course.getGrades();

        for (Double grade : grades) {
            if (grade == 0.0)
                return true;
        }
        return false;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public Double[] getGrades() {
        return grades;
    }

    public void setGrades(Double[] grades) {
        this.grades = grades;
    }

    public double getCourseGrade() {
        return courseGrade;
    }

    public void setCourseGrade(double courseGrade) {
        this.courseGrade = courseGrade;
    }

    // private void computeCourseGrade() {

    // courseGradeTxt.setText(Double.toString(courseGrade));
    // if (isIncludedInGWA) {
    // StudentDashboardController.updateCourseGrade(courseGrade);
    // }
    // }
}
