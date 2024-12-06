
package com.siit.gradetracker.faculty;

import java.net.URL;
import java.sql.*;
import java.util.*;

import com.siit.gradetracker.main.DatabaseConnection;
import com.siit.gradetracker.students.StudentInformation;
import com.siit.gradetracker.util.DisplayError;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class FacultyDashboardController extends FacultyDashboardCentralController {

    private DisplayError de = new DisplayError();
    private int studentCount;
    protected static ObservableList<StudentInformation> studentList = FXCollections.observableArrayList();

    @FXML
    private Button homeBtn;

    @FXML
    private Text studentCountText;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        homeBtn.getStyleClass().add("active");
        fetchInformation();
        studentCountText.setText(Integer.toString(studentCount));
    }

    @Override
    protected void fetchInformation() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            fetchStudents(conn);
        } catch (SQLException e) {
            de.showErrorDialog("Error", "");
        }
    }

    private void fetchStudents(Connection conn) throws SQLException {
        studentList.clear();
        String query;
        if (programId == 0) {
            query = "SELECT si.student_id, si.last_name, si.first_name, p.program_acronym "
                    + "FROM students.student_information si "
                    + "JOIN sgpt.program p ON si.program_id = p.id";
        } else if (programId >= 1 && programId <= 5) {
            query = "SELECT si.student_id, si.last_name, si.first_name, p.program_acronym "
                    + "FROM students.student_information si "
                    + "JOIN sgpt.program p ON si.program_id = p.id "
                    + "WHERE si.program_id = ?";
        } else {
            throw new IllegalArgumentException("Invalid programId value. Expected 0 or between 1-5.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            if (programId >= 1 && programId <= 5) {
                stmt.setInt(1, programId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String studentId = rs.getString("student_id");
                    String lastName = rs.getString("last_name");
                    String firstName = rs.getString("first_name");
                    String programAcronym = rs.getString("program_acronym");
                    studentCount++;

                    StudentInformation student = new StudentInformation(studentId, lastName, firstName, programAcronym);
                    studentList.add(student);
                }

            }
        } catch (SQLException e) {
            de.showErrorDialog("Error", "An error occurred while fetching student information. Please try again.");
        }
    }

}
