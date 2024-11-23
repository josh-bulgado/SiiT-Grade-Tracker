
package com.sgpt.mavenproject1.students;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javafx.fxml.*;
import javafx.scene.text.Text;

import com.sgpt.mavenproject1.main.App;
import com.sgpt.mavenproject1.main.DatabaseConnection;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.*;

public class StudentDashboardController implements Initializable {

    private static String studentId;

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
            }
        });
    }

    private void fetchInformation() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            fetchStudentInformation(conn, stmt, rs);
            fetchStudentSemesters(conn, stmt, rs);
            String currentSemester = schoolYearTerm.getValue();
            if (currentSemester != null) {
                fetchStudentCourse(conn, stmt, rs, currentSemester); // Fetch courses for the current semester
            }
        } catch (SQLException e) {
            e.printStackTrace();
            studentNameTxt.setText("Error fetching student information");
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchStudentInformation(Connection conn, PreparedStatement stmt, ResultSet rs) throws SQLException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        conn = DatabaseConnection.getConnection();
        String query = "SELECT si.*, p.program_name "
                + "FROM students.student_information si "
                + "JOIN sgpt.program p ON si.program_id = p.id "
                + "WHERE student_id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setString(1, studentId);

        rs = stmt.executeQuery();

        if (rs.next()) {
            String fullName = (rs.getString("first_name") != null ? rs.getString("first_name") : "") + " "
                    + (rs.getString("middle_name") != null ? rs.getString("middle_name") : "") + " "
                    + (rs.getString("last_name") != null ? rs.getString("last_name") : "");
            String program = rs.getString("program_name");
            String email = rs.getString("email_address");
            String contact = rs.getString("contact_number");
            String birthdate = dateFormat.format(rs.getDate("birthdate"));

            // Set the data to Text fields
            studentNameTxt.setText(fullName);
            // studentIdTxt.setText(studentId);
            programTxt.setText(program);
            schoolNameTxt.setText("STI College Legazpi");
            emailAddressTxt.setText(email);
            phoneNumberTxt.setText(contact);
            birthdateTxt.setText(birthdate);
        } else {
            studentNameTxt.setText("Student not found");
        }
    }

    private void fetchStudentCourse(Connection conn, PreparedStatement stmt, ResultSet rs, String semester)
            throws SQLException {
        conn = DatabaseConnection.getConnection();
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

        stmt = conn.prepareStatement(query);
        stmt.setString(1, studentId);
        stmt.setString(2, semester);
        rs = stmt.executeQuery();

        coursesPane.getChildren().clear();

        while (rs.next()) {
            String courseDescription = rs.getString("course_description");

            Double prelimsGrade = rs.getDouble("prelims_grade");
            Double midtermGrade = rs.getDouble("midterm_grade");
            Double prefinalGrade = rs.getDouble("prefinals_grade");
            Double finalGrade = rs.getDouble("finals_grade");

            StudentCCController.setCourseInformation(courseDescription, prelimsGrade, midtermGrade, prefinalGrade,
                    finalGrade);
            courseCard = loadCourseCard();
            coursesPane.getChildren().add(courseCard);
        }
    }

    private void updateCourses(String semester) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            fetchStudentCourse(conn, stmt, rs, semester);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchStudentSemesters(Connection conn, PreparedStatement stmt, ResultSet rs) throws SQLException {
        conn = DatabaseConnection.getConnection();
        String query = "SELECT CONCAT (sy.school_year_name, ' ' , t.term_name) AS semester "
                + "FROM students.student_enrollment se "
                + "JOIN students.student_information si ON se.student_id = si.id "
                + "JOIN sgpt.school_year sy ON se.year_id = sy.id "
                + "JOIN sgpt.terms t ON se.term_id = t.id "
                + "WHERE si.student_id = ? "
                + "ORDER BY sy.school_year_name ASC, t.term_name ASC";

        stmt = conn.prepareStatement(query);
        stmt.setString(1, studentId);
        rs = stmt.executeQuery();

        List<String> semesters = new ArrayList<>();

        while (rs.next()) {
            String semester = rs.getString("semester");
            semesters.add(semester);
        }

        Collections.reverse(semesters);
        System.out.println(semesters);

        for (String semester : semesters) {
            schoolYearTerm.getItems().addAll(semester);
        }
        if (!schoolYearTerm.getItems().isEmpty()) {
            schoolYearTerm.setValue(semesters.get(0));
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
