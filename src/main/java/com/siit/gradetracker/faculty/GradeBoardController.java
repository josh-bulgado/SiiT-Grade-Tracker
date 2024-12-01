
package com.siit.gradetracker.faculty;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

import com.siit.gradetracker.SiiTApp;
import com.siit.gradetracker.main.Course;
import com.siit.gradetracker.main.DatabaseConnection;

import javafx.fxml.*;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class GradeBoardController extends FacultySLController {

    private static String studentId;
    private Map<String, List<Course>> coursesBySemester = new TreeMap<>();
    private List<String> semester = new ArrayList<>();;

    @FXML
    private VBox course_section;

    @FXML
    private HBox course_box;

    @FXML
    private Button studentBtn, backBtn;

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
            String currentSemester = semester.get(semester.size() - 1);
            displayStudentCourse(currentSemester);
        } catch (SQLException e) {
            e.getStackTrace();
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
        }
    }

    private String buildFullName(ResultSet rs) throws SQLException {
        return String.join(" ",
                Optional.ofNullable(rs.getString("first_name")).orElse(""),
                Optional.ofNullable(rs.getString("middle_name")).orElse(""),
                Optional.ofNullable(rs.getString("last_name")).orElse(""));
    }

    private void fetchStudentCourse(Connection conn) {
        System.out.println("invoking hehe");
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
            // stmt.setString(2, semester);
            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String semesterResult = rs.getString("semester");
                    String courseCode = rs.getString("course_code");
                    String courseDescription = rs.getString("course_description");
                    int courseUnit = rs.getInt("course_unit");

                    double prelimsGrade = rs.getDouble("prelims_grade");
                    double midtermGrade = rs.getDouble("midterm_grade");
                    double prefinalsGrade = rs.getDouble("prefinals_grade");
                    double finalsGrade = rs.getDouble("finals_grade");

                    double[] grades = { prelimsGrade, midtermGrade, prefinalsGrade, finalsGrade };

                    Course course = new Course(courseCode, courseDescription, courseUnit, grades);
                    coursesBySemester.computeIfAbsent(semesterResult, k -> new ArrayList<>()).add(course);
                }
            }
        } catch (SQLException e) {
            e.getStackTrace();
        }
    }

    private void displayStudentCourse(String semester) {
        List<Course> courses = coursesBySemester.get(semester);

        List<HBox> courseCards = new ArrayList<>();

        for (Course course : courses) {
            course_box = loadStudentCourseCard();
            StudentCourseTabController.setCourse(course.getCourseCode(), course.getCourseDescription(),
                    course.getCourseUnit(), course.getGrades());

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
    private void backToStudentList() throws IOException {
        SiiTApp.setRoot("faculty_student_list");
    }
}
