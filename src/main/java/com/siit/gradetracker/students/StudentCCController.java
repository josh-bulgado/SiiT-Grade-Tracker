
package com.siit.gradetracker.students;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.*;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class StudentCCController implements Initializable {

    private static String courseDescription;
    private static double[] grades = new double[4];
    private static double courseGrade;

    @FXML
    private Text courseName;

    @FXML
    private Text prelims_grade, midterm_grade, prefinal_grade, final_grade;

    @FXML
    private Text courseGradeTxt;

    @FXML
    private ChoiceBox<String> schoolYearTerm;

    @FXML
    private GridPane courseGradePane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        courseName.setText(courseDescription);
        updateGradesDisplay();
        if (isAnyGradeZero()) {
            removeCourseGradePane();
        }
    }

    @FXML
    private void removeCourseGradePane() {

        BorderPane borderPane = (BorderPane) courseGradePane.getParent();

        if (borderPane != null) {
            borderPane.setBottom(null);
        }
    }

    public static void setCourseInformation(String courseDescription, double[] grades, Double courseGrade) {
        StudentCCController.courseDescription = courseDescription;
        StudentCCController.grades = grades;
        StudentCCController.courseGrade = courseGrade;
    }

    private void updateGradesDisplay() {
        prelims_grade.setText(grades[0] != 0 ? formatGrade(grades[0]) : "");
        midterm_grade.setText(grades[1] != 0 ? formatGrade(grades[1]) : "");
        prefinal_grade.setText(grades[2] != 0 ? formatGrade(grades[2]) : "");
        final_grade.setText(grades[3] != 0 ? formatGrade(grades[3]) : "");
        courseGradeTxt.setText(Double.toString(courseGrade));
    }

    private String formatGrade(Double grade) {
        return (grade == grade.intValue()) ? String.format("%d", grade.intValue()) : String.format("%.2f", grade);
    }

    private boolean isAnyGradeZero() {
        for (Double grade : grades) {
            if (grade == 0.0)
                return true;
        }
        return false;
    }

    // private void computeCourseGrade() {

    // courseGradeTxt.setText(Double.toString(courseGrade));
    // if (isIncludedInGWA) {
    // StudentDashboardController.updateCourseGrade(courseGrade);
    // }
    // }
}
