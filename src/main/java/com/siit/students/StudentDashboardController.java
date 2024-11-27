
package com.siit.students;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javafx.fxml.*;
import javafx.scene.text.Text;

import com.siit.App;
import com.siit.main.DatabaseConnection;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.*;

public class StudentDashboardController implements Initializable {

    private static final String DEFAULT_SCHOOL_NAME = "STI College Legazpi";
    private static final String NO_GRADES_TEXT = "0.00";

    private static String studentId;
    private static List<Double> courseGrades = new ArrayList<>();

    @FXML
    private Text studentIdTxt, studentNameTxt, programTxt, schoolNameTxt, emailAddressTxt, phoneNumberTxt, birthdateTxt,
            termGWA;

    @FXML
    private TilePane coursesPane;

    @FXML
    private StackPane courseCard;

    @FXML
    private ChoiceBox<String> schoolYearTerm;

    public static void setStudentId(String studentId) {
        StudentDashboardController.studentId = studentId;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        if (studentId != null) {
            studentIdTxt.setText(studentId);
        }
        fetchInformation();

        schoolYearTerm.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateCourses(newValue);
                updateCurrentGWA();
            }
        });
    }

    private void fetchInformation() {

        try (Connection conn = DatabaseConnection.getConnection()) {
            fetchStudentInformation(conn);
            fetchStudentSemesters(conn);
            String currentSemester = schoolYearTerm.getValue();
            if (currentSemester != null) {
                fetchStudentCourse(conn, currentSemester); // Fetch courses for the current semester
            }
            updateCurrentGWA();
        } catch (SQLException e) {
            e.printStackTrace();
            studentNameTxt.setText("Error fetching student information");
        }
    }

    private void fetchStudentInformation(Connection conn) throws SQLException {
        String query = "SELECT si.*, p.program_name FROM students.student_information si " +
                "JOIN sgpt.program p ON si.program_id = p.id WHERE student_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String fullName = buildFullName(rs);
                    studentNameTxt.setText(fullName);
                    programTxt.setText(rs.getString("program_name"));
                    schoolNameTxt.setText(DEFAULT_SCHOOL_NAME);
                    emailAddressTxt.setText(rs.getString("email_address"));
                    phoneNumberTxt.setText(rs.getString("contact_number"));
                    birthdateTxt.setText(new SimpleDateFormat("MMMM dd, yyyy").format(rs.getDate("birthdate")));
                } else {
                    studentNameTxt.setText("Student not found");
                }
            }
        }
    }

    private String buildFullName(ResultSet rs) throws SQLException {
        return String.join(" ",
                Optional.ofNullable(rs.getString("first_name")).orElse(""),
                Optional.ofNullable(rs.getString("middle_name")).orElse(""),
                Optional.ofNullable(rs.getString("last_name")).orElse(""));
    }

    private void fetchStudentSemesters(Connection conn) throws SQLException {
        String query = "SELECT CONCAT (sy.school_year_name, ' ' , t.term_name) AS semester "
                + "FROM students.student_enrollment se "
                + "JOIN students.student_information si ON se.student_id = si.id "
                + "JOIN sgpt.school_year sy ON se.year_id = sy.id "
                + "JOIN sgpt.terms t ON se.term_id = t.id "
                + "WHERE si.student_id = ? "
                + "ORDER BY sy.school_year_name ASC, t.term_name ASC";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<String> semesters = new ArrayList<>();

                while (rs.next()) {
                    semesters.add(rs.getString("semester"));
                }

                Collections.reverse(semesters);

                for (String semester : semesters) {
                    schoolYearTerm.getItems().addAll(semester);
                }
                if (!schoolYearTerm.getItems().isEmpty()) {
                    schoolYearTerm.setValue(semesters.get(0));
                }
            }
        }

    }

    private void fetchStudentCourse(Connection conn, String semester) throws SQLException {
        String query = "SELECT sg.prelims_grade, sg.midterm_grade, sg.prefinals_grade, sg.finals_grade, c.course_description "
                + "FROM students.student_grades sg "
                + "JOIN students.student_course_2 sc ON sg.student_courses_id = sc.id "
                + "JOIN students.student_enrollment se ON sc.student_enrollment_id = se.id "
                + "JOIN students.student_information si ON se.student_id = si.id "
                + "JOIN sgpt.courses c ON sc.course_id = c.id "
                + "JOIN sgpt.school_year sy ON se.year_id = sy.id "
                + "JOIN sgpt.terms t ON se.term_id = t.id "
                + "WHERE si.student_id = ? "
                + "AND CONCAT (sy.school_year_name, ' ' , t.term_name) = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            stmt.setString(2, semester);
            try (ResultSet rs = stmt.executeQuery()) {
                coursesPane.getChildren().clear();

                while (rs.next()) {
                    String courseDescription = rs.getString("course_description");
                    Double[] grades = {
                            rs.getDouble("prelims_grade"),
                            rs.getDouble("midterm_grade"),
                            rs.getDouble("prefinals_grade"),
                            rs.getDouble("finals_grade")
                    };

                    StudentCCController.setCourseInformation(courseDescription, grades);
                    courseCard = loadCourseCard();
                    coursesPane.getChildren().add(courseCard);
                }
            }
        }

    }

    private void updateCourses(String semester) {
        courseGrades.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            fetchStudentCourse(conn, semester);
            updateCurrentGWA();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateCourseGrade(double courseGrade) {
        courseGrades.add(courseGrade);
    }

    private void updateCurrentGWA() {
        if (!courseGrades.isEmpty()) {
            double total = 0;
            for (double grade : courseGrades) {
                total += grade;
            }
            double gwa = total / courseGrades.size();
            termGWA.setText(String.format("%.2f", gwa)); // Display GWA in the UI
        } else {
            termGWA.setText(NO_GRADES_TEXT);
        }
    }

    private StackPane loadCourseCard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/sgpt/mavenproject1/student_courses_card.fxml"));
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void logoutStudent() throws IOException {
        App.setRoot("login");
    }
}
