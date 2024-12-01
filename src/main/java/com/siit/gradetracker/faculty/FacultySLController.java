
package com.siit.gradetracker.faculty;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import com.siit.gradetracker.main.DatabaseConnection;
import com.siit.gradetracker.students.StudentInformation;
import com.siit.gradetracker.util.DisplayError;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class FacultySLController extends FacultyDashboardCentralController {

    private DisplayError de = new DisplayError();

    @FXML
    private Button studentBtn;

    @FXML
    private ComboBox<String> programComboBox;

    @FXML
    private TableView<StudentInformation> studentTable;

    @FXML
    private TableColumn<String, String> student_id, last_name, first_name, program_acronym;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        studentBtn.getStyleClass().add("active");
        student_id.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        last_name.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        first_name.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        program_acronym.setCellValueFactory(new PropertyValueFactory<>("programAcronym"));
        fetchInformation();

        programComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateStudentTableForSelectedProgram(newValue);
        });

        studentTable.setRowFactory(tv -> {
            TableRow<StudentInformation> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    StudentInformation selectedStudent = row.getItem();
                    // Send the studentId to the Gradeboard
                    goToGradeboard(selectedStudent.getStudentId());
                }
            });
            return row;
        });

    }

    private void fetchInformation() {

        try (Connection conn = DatabaseConnection.getConnection()) {
            fetchPrograms(conn);
            fetchStudents(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            de.showErrorDialog("Error", "An error occurred while fetching student information. Please try again.");
        }
    }

    private void fetchPrograms(Connection conn) throws SQLException {

        String query;
        if (programId == 0) {
            query = "SELECT p.program_acronym FROM sgpt.program p";
        } else if (programId >= 1 && programId <= 5) {
            query = "SELECT p.program_acronym FROM sgpt.program p WHERE p.id = ?";
        } else {
            throw new IllegalArgumentException("Invalid programId value. Expected 0 or between 1-5.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            if (programId >= 1 && programId <= 5) {
                stmt.setInt(1, programId); // Set the programId as a parameter for the query
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String programAcronym = rs.getString("program_acronym");
                    programComboBox.getItems().add(programAcronym);
                }
            }
        }
    }

    private void fetchStudents(Connection conn) throws SQLException {

        String query;
        if (programId == 0) {
            query = "SELECT si.student_id, si.last_name, si.first_name, p.program_acronym "
                    + "FROM students.student_information si "
                    + "JOIN sgpt.program p ON si.program_id = p.id";
        } else if (programId >= 1 && programId <= 5) {
            query = "SELECT si.student_id, si.last_name, si.first_name, p.program_acronym, si.program_id "
                    + "FROM students.student_information si "
                    + "JOIN sgpt.program p ON si.program_id = p.id "
                    + "WHERE si.program_id = ?";
        } else {
            throw new IllegalArgumentException("Invalid programId value. Expected 0 or between 1-5.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            if (programId >= 1 && programId <= 5) {
                stmt.setInt(1, programId); // Set the programId as a parameter for the query
            }
            try (ResultSet rs = stmt.executeQuery()) {
                ObservableList<StudentInformation> students = FXCollections.observableArrayList();

                while (rs.next()) {
                    String studentId = rs.getString("student_id");
                    String lastName = rs.getString("last_name");
                    String firstName = rs.getString("first_name");
                    String programAcronym = rs.getString("program_acronym");
                    students.add(new StudentInformation(studentId, lastName, firstName, programAcronym));
                }
                studentTable.setItems(students);
            }
        } catch (SQLException e) {
            de.showErrorDialog("Error", "An error occurred while fetching student information. Please try again.");
        }

    }

    // This method is called when a user selects a new program from the ComboBox
    private void updateStudentTableForSelectedProgram(String selectedProgram) {
        if (selectedProgram != null) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Fetch the programId corresponding to the selected program acronym
                String query = "SELECT id FROM sgpt.program WHERE program_acronym = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, selectedProgram);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            int selectedProgramId = rs.getInt("id");
                            // Update the global static programId to reflect the selected program
                            FacultyDashboardCentralController.setProgramId(selectedProgramId);
                            fetchStudents(conn); // Fetch students for the selected program
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void goToGradeboard(String studentId) {
        try {
            GradeBoardController.setStudentId(studentId);

            // Use the SiiTApp's setRoot method to navigate to the Gradeboard
            com.siit.gradetracker.SiiTApp.setRoot("faculty_grade_board");
        } catch (IOException e) {
            e.printStackTrace();
            de.showErrorDialog("Error", "An error occurred while fetching student information. Please try again.");
            return;
        }

    }

}
