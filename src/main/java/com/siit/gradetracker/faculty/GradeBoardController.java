
package com.siit.gradetracker.faculty;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import com.siit.gradetracker.SiiTApp;
import com.siit.gradetracker.main.Course;
import com.siit.gradetracker.main.DatabaseConnection;
import com.siit.gradetracker.main.SemesterInfo;
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
    private static final String CURRENT_ACADEMIC_YEAR = "2024 - 2025";
    private static final String CURRENT_SEMESTER = "First Semester";
    String currentSemester = CURRENT_ACADEMIC_YEAR + " " + CURRENT_SEMESTER;

    private List<String> semester = new ArrayList<>();
    private Map<String, SemesterInfo> coursesBySemester = new HashMap<>();
    private boolean isUpdateMode = true;
    // private List<TextField> textFields = new ArrayList<>();
    private DisplayError de = new DisplayError();
    private Map<Integer, List<TextField>> courseTextFieldsMap = new HashMap<>();
    private Map<Integer, List<String>> originalTextFieldValuesMap = new HashMap<>();

    @FXML
    private VBox course_section;

    @FXML
    private HBox course_box;

    @FXML
    private Button studentBtn, backBtn, updateSaveBtn, archiveCancelBtn;

    @FXML
    private TextField prelimTextField, midtermTextField, prefinalTextField, finalTextField;

    @FXML
    private Text student_number, student_name, student_program, year_level, total_unit, semester_gwa;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        studentBtn.getStyleClass().add("active");

        student_number.setText(studentId);

        fetchInformation();

        displayStudentCourse(currentSemester);
        updateTotalUnit(currentSemester);
        updateSemesterGWA(currentSemester);
    }

    public static void setStudentId(String studentId) {
        GradeBoardController.studentId = studentId;
    }

    protected void fetchInformation() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            fetchStudentInformation(conn);
            fetchStudentCourse(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            de.showErrorDialog("Error", "An error occurred while fetching student information. Please try again.");
        }
    }

    private void fetchStudentInformation(Connection conn) {
        String query = "SELECT si.*, yl.level_order, p.program_acronym, CONCAT(yl.level_order, 'Y', t.term_order) AS formatted_year_level "
                + "FROM students.student_enrollment se "
                + "JOIN students.student_information si ON se.student_id = si.id "
                + "JOIN sgpt.program p ON si.program_id = p.id "
                + "JOIN sgpt.year_level yl ON se.year_id = yl.id "
                + "JOIN sgpt.terms t ON se.term_id = t.id "
                + "WHERE si.student_id = ? "
                + "AND t.term_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            stmt.setString(2, CURRENT_SEMESTER);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String fullName = buildFullName(rs);
                    student_name.setText(fullName);
                    student_program.setText(rs.getString("program_acronym"));
                    year_level.setText(rs.getString("formatted_year_level"));
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
        GradeComputation gradeCompute = new GradeComputation();
        String query = "SELECT sg.id, sg.prelims_grade, sg.midterm_grade, sg.prefinals_grade, sg.finals_grade, "
                + "c.*, CONCAT(sy.school_year_name, ' ', t.term_name) AS semester "
                + "FROM students.student_grades sg "
                + "JOIN students.student_course sc ON sg.student_courses_id = sc.id "
                + "JOIN students.student_enrollment se ON sc.student_enrollment_id = se.id "
                + "JOIN students.student_information si ON se.student_id = si.id "
                + "JOIN sgpt.courses c ON sc.course_id = c.id "
                + "JOIN sgpt.school_year sy ON se.year_id = sy.id "
                + "JOIN sgpt.terms t ON se.term_id = t.id "
                + "WHERE si.student_id = ? "
                + "AND sy.school_year_name = ? "
                + "AND t.term_name = ? "
                + "ORDER BY sg.id ASC";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            stmt.setString(2, CURRENT_ACADEMIC_YEAR); // For the current academic year
            stmt.setString(3, CURRENT_SEMESTER); // For the current semester
            try (ResultSet rs = stmt.executeQuery()) {
                Map<String, List<Course>> tempCoursesBySemester = new HashMap<>();
                while (rs.next()) {
                    String semester = rs.getString("semester");
                    int gradeId = rs.getInt("id");
                    String courseCode = rs.getString("course_code");
                    String courseDescription = rs.getString("course_description");
                    int courseUnit = rs.getInt("course_unit");

                    Double[] grades = {
                            Optional.ofNullable(rs.getObject("prelims_grade", Double.class)).orElse(null),
                            Optional.ofNullable(rs.getObject("midterm_grade", Double.class)).orElse(null),
                            Optional.ofNullable(rs.getObject("prefinals_grade", Double.class)).orElse(null),
                            Optional.ofNullable(rs.getObject("finals_grade", Double.class)).orElse(null),
                    };

                    double courseGrade = gradeCompute.computeCourseGrade(grades);
                    boolean isIncludedInGWA = rs.getBoolean("included_in_gwa");

                    Course course = new Course(gradeId, courseCode, courseDescription, courseUnit, grades, courseGrade,
                            isIncludedInGWA);
                    tempCoursesBySemester.computeIfAbsent(semester, k -> new ArrayList<>()).add(course);

                }
                gradeCompute.calculateSemesterGWA(coursesBySemester, tempCoursesBySemester);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            de.showErrorDialog("Error", "An error occurred while fetching student courses. Please try again.");
        }
    }

    private void fetchCurrentSemester(Connection conn) {
        String query = "SELECT CONCAT(sy.school_year_name, ' ', t.term_name) as term "
                + "FROM sgpt.school_year sy "
                + "CROSS JOIN sgpt.terms t "
                + "ORDER BY sy.id, t.id ";

        try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String term = rs.getString("term");
                semester.add(term);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void displayStudentCourse(String semester) {
        course_section.getChildren().clear();

        List<Course> courses = coursesBySemester.get(semester).getCourses();
        List<HBox> courseCards = new ArrayList<>();

        for (Course course : courses) {
            StudentCourseTabController.setCourse(course.getCourseCode(), course.getCourseDescription(),
                    course.getCourseUnit(), course.getGrades());
            course_box = loadStudentCourseCard();
            if (course_box != null) {
                course_box.setId(String.valueOf(course.getGradeId())); // Set the ID to the course's grade ID
                courseCards.add(course_box);
            }
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
            archiveCancelBtn.setText("CANCEL");
        } else {
            saveGrades();
            updateSaveBtn.setText("UPDATE");
            archiveCancelBtn.setText("ARCHIVE");
        }

        isUpdateMode = !isUpdateMode;
    }

    @FXML
    private void updateStudentGrades() {

        for (Node node : course_section.getChildren()) {
            if (node instanceof HBox) {
                HBox courseCard = (HBox) node;

                prelimTextField = (TextField) courseCard.lookup("#prelim_grade");
                midtermTextField = (TextField) courseCard.lookup("#midterm_grade");
                prefinalTextField = (TextField) courseCard.lookup("#prefinal_grade");
                finalTextField = (TextField) courseCard.lookup("#final_grade");

                try {
                    int courseId = Integer.parseInt(courseCard.getId()); // Ensure the ID is numeric

                    List<TextField> textFields = Arrays.asList(prelimTextField, midtermTextField, prefinalTextField,
                            finalTextField);
                    courseTextFieldsMap.put(courseId, textFields);

                    List<String> originalValues = textFields.stream()
                            .map(TextField::getText)
                            .collect(Collectors.toList());
                    originalTextFieldValuesMap.put(courseId, originalValues);

                    changeTextField(textFields, true);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    // Handle the case where the ID is not numeric
                }
            }
        }

    }

    @FXML
    private void saveGrades() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            for (Course course : coursesBySemester.get(currentSemester).getCourses()) {
                String updateQuery = "UPDATE students.student_grades SET "
                        + "prelims_grade = ?, midterm_grade = ?, prefinals_grade = ?, finals_grade = ? "
                        + "WHERE id = ?";

                int gradeId = course.getGradeId();
                List<TextField> textFields = courseTextFieldsMap.get(gradeId);
                Double[] grades = {

                        parseNullableDouble(textFields.get(0).getText()),
                        parseNullableDouble(textFields.get(1).getText()),
                        parseNullableDouble(textFields.get(2).getText()),
                        parseNullableDouble(textFields.get(3).getText())
                };

                try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                    for (int i = 0; i < grades.length; i++) {
                        if (grades[i] != null) {
                            stmt.setDouble(i + 1, grades[i]);
                        } else {
                            stmt.setNull(i + 1, java.sql.Types.DOUBLE);
                        }
                    }
                    stmt.setInt(5, gradeId);
                    stmt.executeUpdate();
                }
                changeTextField(textFields, false); // Set the TextFields back to non-editable
            }
        } catch (SQLException e) {
            e.printStackTrace();
            de.showErrorDialog("Error", "An error occurred while saving grades. Please try again.");
        }

    }

    @FXML
    private void handleArchiveCancelButton() {
        if (isUpdateMode) {
        } else {
            cancelGradeUpdate();
            updateSaveBtn.setText("UPDATE");
            archiveCancelBtn.setText("ARCHIVE");

        }
        isUpdateMode = !isUpdateMode;
    }

    private void archiveStudentGrades() {

    }

    @FXML
    private void cancelGradeUpdate() {
        for (Node node : course_section.getChildren()) {
            if (node instanceof HBox) {
                HBox courseCard = (HBox) node;

                prelimTextField = (TextField) courseCard.lookup("#prelim_grade");
                midtermTextField = (TextField) courseCard.lookup("#midterm_grade");
                prefinalTextField = (TextField) courseCard.lookup("#prefinal_grade");
                finalTextField = (TextField) courseCard.lookup("#final_grade");

                try {
                    int courseId = Integer.parseInt(courseCard.getId()); // Ensure the ID is numeric

                    List<TextField> textFields = Arrays.asList(prelimTextField, midtermTextField, prefinalTextField,
                            finalTextField);
                    courseTextFieldsMap.put(courseId, textFields);

                    // Revert to original values
                    List<String> originalValues = originalTextFieldValuesMap.get(courseId);
                    if (originalValues != null) {
                        for (int i = 0; i < textFields.size(); i++) {
                            textFields.get(i).setText(originalValues.get(i));
                        }
                    }

                    changeTextField(textFields, false);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void changeTextField(List<TextField> textFields, boolean isEditable) {

        for (TextField field : textFields) {
            field.setEditable(isEditable);
            TextFieldUtils.makeTextFieldDouble(field);
            if (isEditable) {
                field.getStyleClass().add("text-field-is-editable");
                field.getStyleClass().remove("text-field-is-not-editable");
            } else {
                field.getStyleClass().remove("text-field-is-editable");
                field.getStyleClass().add("text-field-is-not-editable");
            }
        }
    }

    private Double parseNullableDouble(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null; 
        }
    }

    @FXML
    private void updateTotalUnit(String semester) {
        total_unit.setText(Integer.toString(coursesBySemester.get(semester).getOverAllUnits()));
    }

    @FXML
    private void updateSemesterGWA(String semester) {
        semester_gwa.setText(String.format("%.2f", coursesBySemester.get(semester).getGwa()));
    }

    @FXML
    private void backToStudentList() throws IOException {
        SiiTApp.setRoot("faculty_student_list");
    }
}
