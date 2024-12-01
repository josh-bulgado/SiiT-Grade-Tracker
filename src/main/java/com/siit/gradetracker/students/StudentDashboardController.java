package com.siit.gradetracker.students;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javafx.fxml.*;
import javafx.scene.text.Text;
import com.siit.gradetracker.SiiTApp;
import com.siit.gradetracker.main.Course;
import com.siit.gradetracker.main.DatabaseConnection;
import com.siit.gradetracker.main.SemesterInfo;
import com.siit.gradetracker.util.GradeComputation;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.*;

public class StudentDashboardController implements Initializable {

    private static final String DEFAULT_SCHOOL_NAME = "STI College Legazpi";
    private static final String NO_GRADES_TEXT = "0.00";

    private static String studentId;
    private List<Double> courseGrades = new ArrayList<>();
    private List<Double> previousGWAs = new ArrayList<>();
    private Map<String, SemesterInfo> coursesBySemester = new HashMap<>();

    @FXML
    private Text studentIdTxt, studentNameTxt, programTxt, schoolNameTxt, emailAddressTxt, phoneNumberTxt, birthdateTxt,
            termGWA, cumulativeGWATxt;

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

        // Listener for when a semester is selected to display corresponding courses and
        // GWA
        schoolYearTerm.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayCoursesForSelectedSemester(newValue);
                displaySemesterGWA(newValue);
                // updateCurrentGWA(newValue);
            }
        });
    }

    private void fetchInformation() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            fetchStudentInformation(conn); // Fetch student basic information
            fetchStudentSemesters(conn); // Fetch available semesters
            fetchStudentCourses(conn); // Fetch all courses grouped by semester
            updateCumulativeGWA(); // Calculate cumulative GWA for all semesters

            String currentTerm = schoolYearTerm.getValue();
            if (currentTerm != null) {
                displayCoursesForSelectedSemester(currentTerm); // Display courses for the first semester
                updateCurrentGWA(currentTerm); // Update GWA for the first semester
            }
        } catch (SQLException e) {
            e.printStackTrace();
            studentNameTxt.setText("Error fetching student information");
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
                    schoolYearTerm.setValue(semesters.get(0)); // Set the first semester by default
                }
            }
        }
    }

    private void fetchStudentCourses(Connection conn) throws SQLException {
        GradeComputation gradeCompute = new GradeComputation();
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
                Map<String, List<Course>> tempCoursesBySemester = new HashMap<>();
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

                    double courseGrade = gradeCompute.computeCourseGrade(grades);
                    boolean isIncludedInGWA = rs.getBoolean("included_in_gwa");
                    Course course = new Course(courseCode, courseDescription, courseUnit, grades, courseGrade,
                            isIncludedInGWA);
                    tempCoursesBySemester.computeIfAbsent(semester, k -> new ArrayList<>()).add(course);
                }

                calculateSemesterGWA(tempCoursesBySemester);
            }
        }
    }

    private void calculateSemesterGWA(Map<String, List<Course>> tempCoursesBySemester) {
        for (Map.Entry<String, List<Course>> entry : tempCoursesBySemester.entrySet()) {
            String semester = entry.getKey();
            List<Course> courses = entry.getValue();

            // Calculate the GWA for the semester
            double totalUnits = 0;
            double totalCredits = 0;
            int overAllUnits = 0;

            for (Course course : courses) {
                overAllUnits += course.getCourseUnit();
                if (course.isIncludedInGWA()) {
                    totalUnits += course.getCourseUnit();
                    totalCredits += course.getCourseGrade() * course.getCourseUnit();
                }
            }

            double gwa = totalUnits > 0 ? totalCredits / totalUnits : 0.0;

            // Store the SemesterInfo (list of courses and calculated GWA) in the map
            coursesBySemester.put(semester, new SemesterInfo(courses, gwa, overAllUnits));
        }
    }

    private void displaySemesterGWA(String semester) {
        termGWA.setText(String.format("%.2f", coursesBySemester.get(semester).getGwa()));

    }

    private void displayCoursesForSelectedSemester(String semester) {
        coursesPane.getChildren().clear();
        List<Course> courses = coursesBySemester.get(semester).getCourses();

        if (courses != null) {
            for (Course course : courses) {
                StudentCCController.setCourseInformation(course.getCourseDescription(), course.getGrades(),
                        course.getCourseGrade());
                courseCard = loadCourseCard();
                coursesPane.getChildren().add(courseCard);
            }
        } else {
            System.out.println("No courses found for semester: " + semester);
        }
    }

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
                termGWA.setText(String.format("%.2f", currentGWA));
            } else {
                termGWA.setText(NO_GRADES_TEXT);
            }
        } else {
            termGWA.setText(NO_GRADES_TEXT);
        }
    }

    private void updateCumulativeGWA() {
        double totalGWA = 0.0;

        for (Map.Entry<String, SemesterInfo> term : coursesBySemester.entrySet()) {
            totalGWA += term.getValue().getGwa();
        }

        if (totalGWA != 0.0) {
            double cumulativeGWA = totalGWA / coursesBySemester.size();
            cumulativeGWATxt.setText(String.format("%.2f", cumulativeGWA));
        } else {
            cumulativeGWATxt.setText(NO_GRADES_TEXT);

        }
    }

    @FXML
    private StackPane loadCourseCard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/siit/gradetracker/student_courses_card.fxml"));
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void logoutStudent() throws IOException {
        courseGrades.clear();
        previousGWAs.clear();
        coursesBySemester.clear();
        termGWA.setText(NO_GRADES_TEXT);
        SiiTApp.setRoot("login");
    }
}
