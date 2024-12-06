package com.siit.gradetracker.faculty;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import com.siit.gradetracker.SiiTApp;
import com.siit.gradetracker.main.DatabaseConnection;
import com.siit.gradetracker.students.StudentInformation;
import com.siit.gradetracker.util.*;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public abstract class FacultyBaseListController extends FacultyDashboardController {
  private DisplayError de = new DisplayError();

  protected int programId;

  @FXML
  private TextField studentIdSearchField;

  @FXML
  protected ComboBox<String> programComboBox;

  @FXML
  protected TableView<StudentInformation> studentTable;

  @FXML
  protected TableColumn<StudentInformation, String> studentIdColumn, lastNameColumn, firstNameColumn,
      programAcronymColumn;

  private FilteredList<StudentInformation> filteredStudents;

  @Override
  public void initialize(URL url, ResourceBundle rb) {

    studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
    lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
    firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
    programAcronymColumn.setCellValueFactory(new PropertyValueFactory<>("programAcronym"));

    filteredStudents = new FilteredList<>(studentList, p -> true);

    programComboBox.getSelectionModel().clearSelection();
    fetchInformation();
    populateStudentsToTable();
    filterComboBox();
    fetchStudentRowInformation();
    filterSearchField();
  }

  protected void fetchInformation() {

    try (Connection conn = DatabaseConnection.getConnection()) {
      fetchPrograms(conn);
      fetchStudents(conn);
    } catch (SQLException e) {
      e.printStackTrace();
      de.showErrorDialog("Error", "An error occurred while fetching student information. Please try again.");
    }
  }

  protected void fetchPrograms(Connection conn) throws SQLException {

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
        programComboBox.getItems().add("All");
        while (rs.next()) {
          String programAcronym = rs.getString("program_acronym");
          programComboBox.getItems().add(programAcronym);
        }
      }
    }
  }

  protected void populateStudentsToTable() {
    studentTable.setItems(studentList); // Use the inherited students list
  }

  protected void fetchStudents(Connection conn) throws SQLException {
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

          StudentInformation student = new StudentInformation(studentId, lastName, firstName, programAcronym);
          studentList.add(student);
        }

      }
    } catch (SQLException e) {
      de.showErrorDialog("Error", "An error occurred while fetching student information. Please try again.");
    }
  }

  protected void filterSearchField() {
    studentIdSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
      TextFieldUtils.makeTextFieldInteger(studentIdSearchField);
      filteredStudents.setPredicate(student -> {

        // If filter text is empty, display all students
        if (newValue == null || newValue.isEmpty()) {
          return true;
        }
        // Compare studentId with filter text
        String lowerCaseFilter = newValue.toLowerCase();
        return student.getStudentId().toLowerCase().contains(lowerCaseFilter);
      });
      // Update the table to show the filtered data
      studentTable.setItems(filteredStudents);
    });
  }

  protected void filterComboBox() {
    if (programId == 0) {
      programComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        updateStudentTableForSelectedProgram(newValue);
      });
    } else {
      removeComoBox();
    }
  }

  protected void fetchStudentRowInformation() {
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

  // This method is called when a user selects a new program from the ComboBox
  protected void updateStudentTableForSelectedProgram(String selectedProgram) {
    if (selectedProgram == null) {
      return;
    }

    try (Connection conn = DatabaseConnection.getConnection()) {
      if ("All".equals(selectedProgram)) {
        this.programId = 0;
        fetchStudents(conn);
        return;
      }

      String query = "SELECT id FROM sgpt.program WHERE program_acronym = ?";
      try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, selectedProgram);
        try (ResultSet rs = stmt.executeQuery()) {
          if (rs.next()) {
            int selectedProgramId = rs.getInt("id");
            this.programId = selectedProgramId;
            fetchStudents(conn);
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  protected void goToGradeboard(String studentId) {
    try {
      GradeBoardController.setStudentId(studentId);
      SiiTApp.setRoot("faculty_grade_board");
    } catch (IOException e) {
      e.printStackTrace();
      de.showErrorDialog("Error", "An error occurred while fetching student information. Please try again.");
      return;
    }
  }

  private void removeComoBox() {
    HBox hbox = (HBox) programComboBox.getParent();

    if (hbox != null) {
      hbox.getChildren().remove(programComboBox);
    }
  }
}
