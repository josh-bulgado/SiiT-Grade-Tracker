
package com.siit.gradetracker.faculty;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import com.siit.gradetracker.SiiTApp;
import com.siit.gradetracker.main.Course;
import com.siit.gradetracker.main.DatabaseConnection;

import javafx.fxml.*;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class GradeBoardController extends FacultySLController {

    private static String studentId;
    private Map<String, List<Course>> coursesBySemester = new HashMap<>();

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
            e.getStackTrace();
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
                + "WHERE si.student_id = ? AND semester = 2023 - 2024 Second Semester "
                + "ORDER BY sg.id ASC";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String courseCode = rs.getString("course_code");
                    String courseDescription = rs.getString("course_description");
                    int courseUnit = rs.getInt("course_unit");

                    Double[] grades = {
                            rs.getDouble("prelims_grade"),
                            rs.getDouble("midterm_grade"),
                            rs.getDouble("prefinals_grade"),
                            rs.getDouble("finals_grade")
                    };

                    String semester = rs.getString("semester");

                    Course course = new Course(courseDescription, grades, 0.0, courseUnit, false, courseCode);
                    coursesBySemester.computeIfAbsent(semester, k -> new ArrayList<>()).add(course);
                }
            }
        } catch (SQLException e) {
            e.getStackTrace();
        }
    }

    @FXML
    private void backToStudentList() throws IOException {
        SiiTApp.setRoot("faculty_student_list");
    }
}