
package com.siit.gradetracker.faculty;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

import com.siit.gradetracker.SiiTApp;
import com.siit.gradetracker.main.Course;
import com.siit.gradetracker.main.DatabaseConnection;
import com.siit.gradetracker.util.*;

import javafx.fxml.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.Node;

public class GradeBoardController extends FacultySLController {

    private static String studentId;
    private Map<String, List<Course>> coursesBySemester = new HashMap<>();
    private List<String> semester = new ArrayList<>();
    private boolean isUpdateMode = true;
    private List<TextField> textFields = new ArrayList<>();
    private DisplayError de = new DisplayError();

    @FXML
    private VBox course_section;

    @FXML
    private HBox course_box;

    @FXML
    private Button studentBtn, backBtn, updateSaveBtn;

    @FXML
    private TextField prelimTextField, midtermTextField, prefinalTextField, finalTextField;

    @FXML
    private Text student_number, student_name, student_program, year_level;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        studentBtn.getStyleClass().add("active");
        student_number.setText(studentId);
        fetchInformation();
    }

    public static void setStudentId(String studentId) {
        GradeBoardController.studentId = studentId;
    }

    private void fetchInformation() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            fetchStudentInformation(conn);
            fetchStudentCourse(conn);

            semester.addAll(coursesBySemester.keySet());
            String currentSemester = semester.get(semester.size() - 2);
            displayStudentCourse(currentSemester);
        } catch (SQLException e) {
            e.printStackTrace();
            de.showErrorDialog("Error", "An error occurred while fetching student information. Please try again.");
        }
    }

    private void fetchStudentInformation(Connection conn) {
        String query = "SELECT si.* , yl.level_name, p.program_acronym "
                + "FROM students.student_information si "
                + "JOIN sgpt.year_level yl ON si.year_id = yl.id "
                + "JOIN sgpt.program p ON si.program_id = p.id "
                + "WHERE si.student_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String fullName = buildFullName(rs);
                    student_name.setText(fullName);
                    student_program.setText(rs.getString("program_acronym"));
                    year_level.setText(rs.getString("level_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            de.showErrorDialog("Error", "An error occurred while fetching student information. Please try again.");
        }
    }

    private String buildFullName(ResultSet rs) throws SQLException {
        return String.join(" ",
                Optional.ofNullable(rs.getString("first_name")).orElse(""),
                Optional.ofNullable(rs.getString("middle_name")).orElse(""),
                Optional.ofNullable(rs.getString("last_name")).orElse(""));
    }

    private void fetchStudentCourse(Connection conn) {
        String query = "SELECT sg.prelims_grade, sg.midterm_grade, sg.prefinals_grade, sg.finals_grade, "
                + "c.*, CONCAT(sy.school_year_name, ' ', t.term_name) AS semester "
                + "FROM students.student_grades sg "
                + "JOIN students.student_course sc ON sg.student_courses_id = sc.id "
                + "JOIN students.student_enrollment se ON sc.student_enrollment_id = se.id "
                + "JOIN students.student_information si ON se.student_id = si.id "
                + "JOIN sgpt.courses c ON sc.course_id = c.id "
                + "JOIN sgpt.school_year sy ON se.year_id = sy.id "
                + "JOIN sgpt.terms t ON se.term_id = t.id "
                + "WHERE si.student_id = ? "
                + "ORDER BY sg.id ASC";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String semester = rs.getString("semester");
                    String courseCode = rs.getString("course_code");
                    String courseDescription = rs.getString("course_description");
                    int courseUnit = rs.getInt("course_unit");

                    double[] grades = {
                            rs.getDouble("prelims_grade"),
                            rs.getDouble("midterm_grade"),
                            rs.getDouble("prefinals_grade"),
                            rs.getDouble("finals_grade")
                    };

                    Course course = new Course(courseCode, courseDescription, courseUnit, grades);
                    coursesBySemester.computeIfAbsent(semester, k -> new ArrayList<>()).add(course);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            de.showErrorDialog("Error", "An error occurred while fetching student courses. Please try again.");
        }
    }

    private void displayStudentCourse(String semester) {
        course_section.getChildren().clear();

        List<Course> courses = coursesBySemester.get(semester);
        List<HBox> courseCards = new ArrayList<>();

        for (Course course : courses) {
            StudentCourseTabController.setCourse(course.getCourseCode(), course.getCourseDescription(),
                    course.getCourseUnit(), course.getGrades());
            course_box = loadStudentCourseCard();

            courseCards.add(course_box);
        }

        course_section.getChildren().addAll(courseCards);
    }

    private HBox loadStudentCourseCard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/siit/gradetracker/student_course_tab.fxml"));
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void handleUpdateSaveButton() {
        if (isUpdateMode) {
            updateStudentGrades();
            updateSaveBtn.setText("SAVE");
        } else {
            saveGrades();
            updateSaveBtn.setText("UPDATE");
        }

        isUpdateMode = !isUpdateMode;
    }

    @FXML
    private void updateStudentGrades() {
        for (Node node : course_section.getChildren()) {
            // Check if the node is an instance of HBox
            if (node instanceof HBox) {
                HBox courseCard = (HBox) node;

                // Lookup the TextFields within the HBox
                prelimTextField = (TextField) courseCard.lookup("#prelim_grade");
                midtermTextField = (TextField) courseCard.lookup("#midterm_grade");
                prefinalTextField = (TextField) courseCard.lookup("#prefinal_grade");
                finalTextField = (TextField) courseCard.lookup("#final_grade");
                textFields.add(prelimTextField);
                textFields.add(midtermTextField);
                textFields.add(prefinalTextField);
                textFields.add(finalTextField);

                changeTextField(textFields, true);

            }
        }
    }

    @FXML
    private void saveGrades() {

        changeTextField(textFields, false);
    }

    @FXML
    private void changeTextField(List<TextField> textFields, boolean isEditable) {

        for (TextField field : textFields) {
            field.setEditable(isEditable);
            if (isEditable) {
                field.getStyleClass().add("text-field-is-editable");
                field.getStyleClass().remove("text-field-is-not-editable");
            } else {
                field.getStyleClass().remove("text-field-is-editable");
                field.getStyleClass().add("text-field-is-not-editable");
            }
        }
    }

    @FXML
    private void backToStudentList() throws IOException {
        SiiTApp.setRoot("faculty_student_list");
    }
}
