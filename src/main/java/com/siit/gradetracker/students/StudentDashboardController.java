package com.siit.gradetracker.students;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javafx.application.Platform;
import javafx.fxml.*;

import com.siit.gradetracker.SiiTApp;
import com.siit.gradetracker.main.Course;
import com.siit.gradetracker.main.DatabaseConnection;
import com.siit.gradetracker.main.SemesterInfo;
import com.siit.gradetracker.util.GradeComputation;

import javafx.scene.text.Text;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;

public class StudentDashboardController implements Initializable {

    private static final String DEFAULT_SCHOOL_NAME = "STI College Legazpi";
    private static final String NO_GRADES_TEXT = "0.00";

    private static String studentId;
    private Map<String, SemesterInfo> coursesBySemester = new HashMap<>();

    @FXML
    private Text studentAchievementText, studentIdText, studentNameText, programText, schoolNameText, emailAddressText,
            phoneNumberText,
            birthdateText, termGWAText, cumulativeGWAText;

    @FXML
    private TilePane coursesTilePane;

    @FXML
    private StackPane courseStackPane;

    @FXML
    private ChoiceBox<String> semesterChoiceBox;

    public static void setStudentId(String studentId) {
        StudentDashboardController.studentId = studentId;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        

        if (studentId != null) {
            studentIdText.setText(studentId);
        }
        fetchInformation();
        updateCumulativeGWA(); // Calculate cumulative GWA for all semesters
        updateAchievementText();
        comboBoxSemesterFilter();
        refreshKey();

    }

    private void fetchInformation() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            fetchStudentInformation(conn); // Fetch student basic information
            fetchStudentSemesters(conn); // Fetch available semesters
            fetchStudentCourses(conn); // Fetch all courses grouped by semester

            String currentTerm = semesterChoiceBox.getValue();
            if (currentTerm != null) {
                displayCoursesForSelectedSemester(currentTerm); // Display courses for the first semester
                updateCurrentGWA(currentTerm); // Update GWA for the first semester
            }
        } catch (SQLException e) {
            e.printStackTrace();
            studentNameText.setText("Error fetching student information");
        }
    }

    private void fetchStudentInformation(Connection conn) throws SQLException {
        String query = "SELECT si.*, p.program_name FROM students.student_information si "
                + "JOIN sgpt.program p ON si.program_id = p.id WHERE student_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String fullName = buildFullName(rs);
                    studentNameText.setText(fullName);
                    programText.setText(rs.getString("program_name"));
                    schoolNameText.setText(DEFAULT_SCHOOL_NAME);
                    emailAddressText.setText(rs.getString("email_address"));
                    phoneNumberText.setText(rs.getString("contact_number"));
                    birthdateText.setText(new SimpleDateFormat("MMMM dd, yyyy").format(rs.getDate("birthdate")));
                } else {
                    studentNameText.setText("Student not found");
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
                    semesterChoiceBox.getItems().addAll(semester);
                }

                if (!semesterChoiceBox.getItems().isEmpty()) {
                    semesterChoiceBox.setValue(semesters.get(0)); // Set the first semester by default
                }
            }
        }
    }

    private void fetchStudentCourses(Connection conn) throws SQLException {
        GradeComputation gradeCompute = new GradeComputation();
        String query = "SELECT asg.prelims_grade, asg.midterm_grade, asg.prefinals_grade, asg.finals_grade, "
                + "c.*, CONCAT(sy.school_year_name, ' ', t.term_name) AS semester "
                + "FROM students.all_student_grades asg "
                + "JOIN students.student_course sc ON asg.student_courses_id = sc.id "
                + "JOIN students.student_enrollment se ON sc.student_enrollment_id = se.id "
                + "JOIN students.student_information si ON se.student_id = si.id "
                + "JOIN sgpt.courses c ON sc.course_id = c.id "
                + "JOIN sgpt.school_year sy ON se.year_id = sy.id "
                + "JOIN sgpt.terms t ON se.term_id = t.id "
                + "WHERE si.student_id = ? "
                + "ORDER BY asg.id ASC";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {

                Map<String, List<Course>> tempCoursesBySemester = new HashMap<>();
                while (rs.next()) {
                    String semester = rs.getString("semester");
                    String courseDescription = rs.getString("course_description");
                    int courseUnit = rs.getInt("course_unit");

                    Double[] grades = {
                            rs.getDouble("prelims_grade"),
                            rs.getDouble("midterm_grade"),
                            rs.getDouble("prefinals_grade"),
                            rs.getDouble("finals_grade")
                    };

                    Double courseGrade = gradeCompute.computeCourseGrade(grades);
                    if (courseGrade <= 2.25) {

                    }
                    boolean isIncludedInGWA = rs.getBoolean("included_in_gwa");
                    Course course = new Course(courseDescription, courseUnit, grades, courseGrade, isIncludedInGWA);
                    tempCoursesBySemester.computeIfAbsent(semester, k -> new ArrayList<>()).add(course);
                }

                gradeCompute.calculateSemesterGWA(coursesBySemester, tempCoursesBySemester);
            }
        }
    }

    private void displaySemesterGWA(String semester) {
        termGWAText.setText(String.format("%.2f", coursesBySemester.get(semester).getGwa()));

    }

    private void displayCoursesForSelectedSemester(String semester) {
        coursesTilePane.getChildren().clear();
        List<Course> courses = coursesBySemester.get(semester).getCourses();

        if (courses != null) {
            for (Course course : courses) {

                courseStackPane = loadCourseCard(course);

                coursesTilePane.getChildren().add(courseStackPane);
            }
        } else {
            System.out.println("No courses found for semester: " + semester);
        }
    }

    @FXML
    private void updateCurrentGWA(String semester) {
        double totalCredits = 0.0;
        int totalUnits = 0;
        List<Course> selectedSemesterCourses = coursesBySemester.get(semester).getCourses();

        if (selectedSemesterCourses != null && !selectedSemesterCourses.isEmpty()) {
            for (Course course : selectedSemesterCourses) {
                if (course.isIncludedInGWA()) {
                    double courseGrade = course.getCourseGrade();
                    int courseUnit = course.getCourseUnit();
                    totalCredits += courseGrade * courseUnit;
                    totalUnits += courseUnit;
                }
            }

            if (totalUnits > 0) {
                double currentGWA = totalCredits / totalUnits;
                termGWAText.setText(String.format("%.2f", currentGWA));
            } else {
                termGWAText.setText(NO_GRADES_TEXT);
            }
        } else {
            termGWAText.setText(NO_GRADES_TEXT);
        }
    }

    @FXML
    private void updateCumulativeGWA() {
        double totalGWA = 0.0;

        for (Map.Entry<String, SemesterInfo> term : coursesBySemester.entrySet()) {
            totalGWA += term.getValue().getGwa();
        }

        if (totalGWA != 0.0) {
            double cumulativeGWA = totalGWA / coursesBySemester.size();
            cumulativeGWAText.setText(String.format("%.2f", cumulativeGWA));
        } else {
            cumulativeGWAText.setText(NO_GRADES_TEXT);

        }
    }

    @FXML
    private void updateAchievementText() {

    }

    @FXML
    private StackPane loadCourseCard(Course course) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/siit/gradetracker/student_courses_card.fxml"));
            StackPane courseStackPane = loader.load();

            StudentCCController controller = loader.getController();
            controller.setCourse(course);

            return courseStackPane;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void comboBoxSemesterFilter() {
        semesterChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayCoursesForSelectedSemester(newValue);
                displaySemesterGWA(newValue);
            }
        });
    }

    private void refreshDashboard() {
        coursesBySemester.clear(); // Clear existing data
        fetchInformation(); // Re-fetch and update information
        System.out.println("Dashboard refreshed!");
    }

    private void refreshKey() {
        Platform.runLater(() -> {
            Scene scene = SiiTApp.getScene(); // Access the scene via SiiTApp
            if (scene != null) {
                scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if ((event.isMetaDown() || event.isControlDown()) && event.getCode() == KeyCode.R) {
                        refreshDashboard();
                        event.consume(); // Prevent other handlers from processing this event
                    }
                });
            }
        });
    }

    @FXML
    private void logoutStudent() throws IOException {
        coursesBySemester.clear();
        termGWAText.setText(NO_GRADES_TEXT);
        SiiTApp.setRoot("login");
    }
}
