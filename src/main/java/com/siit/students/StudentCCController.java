
package com.siit.students;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.*;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class StudentCCController implements Initializable {

    private static String courseDescription;
    private static Double prelimGrade, midtermGrade, prefinalGrade, finalGrade;
    private static Double[] grades = new Double[4];

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
            courseGradePane.setVisible(false);
        } else {
            computeCourseGrade();
        }
    }

    public static void setCourseInformation(String courseDescription, Double[] grades) {
        StudentCCController.courseDescription = courseDescription;
        StudentCCController.grades = grades;
    }

    private void updateGradesDisplay() {
        prelims_grade.setText(formatGrade(grades[0]));
        midterm_grade.setText(formatGrade(grades[1]));
        prefinal_grade.setText(formatGrade(grades[2]));
        final_grade.setText(formatGrade(grades[3]));
    }

    private String formatGrade(Double grade) {
        return grade != 0.0 ? String.format("%.2f", grade) : "";
    }

    private boolean isAnyGradeZero() {
        for (Double grade : grades) {
            if (grade == 0.0)
                return true;
        }
        return false;
    }

    private void computeCourseGrade() {
        // Partial Scores

        Double prelimsPS = grades[0] * 0.2;
        Double midtermPS = grades[1] * 0.2;
        Double prefinalPS = grades[2] * 0.2;
        Double finalPS = grades[3] * 0.4;
        Double courseScore = prelimsPS + midtermPS + prefinalPS + finalPS;
        double courseGrade = getCourseGrade(courseScore);

        courseGradeTxt.setText(formatDouble(courseGrade));
        StudentDashboardController.updateCourseGrade(courseGrade);
    }

    private double getCourseGrade(double courseScore) {
        double[] thresholds = { 97.5, 94.5, 91.5, 88.5, 85.5, 81.5, 77.5, 73.5, 69.5, 0.0 };
        double[] grades = { 1.00, 1.25, 1.50, 1.75, 2.00, 2.25, 2.50, 2.75, 3.00, 5.00 };
        for (int i = 0; i < thresholds.length; i++) {
            if (courseScore >= thresholds[i]) {
                return grades[i];
            }
        }
        return 5.00;
    }

    private String formatDouble(double value) {
        if (value % 1 == 0) {
            return String.format("%.0f", value);
        } else {
            return String.format("%.2f", value);
        }
    }
}
