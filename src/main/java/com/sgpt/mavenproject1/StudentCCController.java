
package com.sgpt.mavenproject1;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.*;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class StudentCCController implements Initializable {

    private static String courseDescription;
    private static Double prelimGrade, midtermGrade, prefinalGrade, finalGrade;

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
        prelims_grade.setText(prelimGrade != 0.0 ? formatDouble(prelimGrade) : "");
        midterm_grade.setText(midtermGrade != 0.0 ? formatDouble(midtermGrade) : "");
        prefinal_grade.setText(prefinalGrade != 0.0 ? formatDouble(prefinalGrade) : "");
        final_grade.setText(finalGrade != 0.0 ? formatDouble(finalGrade) : "");
        if (prelimGrade == 0.0 || midtermGrade == 0.0 || prefinalGrade == 0.0 | finalGrade == 0.0) {
            courseGradePane.setVisible(false);
        } else {
            computeCourseGrade();
        }
    }

    public static void setCourseInformation(String courseDescription, Double prelimGrade,
            Double midtermGrade, Double prefinalGrade, Double finalGrade) {
        StudentCCController.courseDescription = courseDescription;
        StudentCCController.prelimGrade = prelimGrade;
        StudentCCController.midtermGrade = midtermGrade;
        StudentCCController.prefinalGrade = prefinalGrade;
        StudentCCController.finalGrade = finalGrade;
    }

    private void computeCourseGrade() {
        // Partial Scores

        Double prelimsPS = prelimGrade * 0.2;
        Double midtermPS = midtermGrade * .2;
        Double prefinalPS = prefinalGrade * .2;
        Double finalPS = finalGrade * .4;
        Double courseScore = prelimsPS + midtermPS + prefinalPS + finalPS;
        double courseGrade = getCourseGrade(courseScore);

        courseGradeTxt.setText(String.format("%.2f", courseGrade));
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
