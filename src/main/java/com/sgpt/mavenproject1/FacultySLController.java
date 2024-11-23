
package com.sgpt.mavenproject1;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import com.sgpt.mavenproject1.main.DatabaseConnection;
import com.sgpt.mavenproject1.students.StudentInformation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class FacultySLController extends FacultyDashboardCentralController {

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
    }

    private void fetchInformation() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {

            fetchPrograms(conn, stmt, rs);
            fetchStudents(conn, stmt, rs);
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

    private void fetchPrograms(Connection conn, PreparedStatement stmt, ResultSet rs) throws SQLException {
        conn = DatabaseConnection.getConnection();

        String sql = "SELECT p.program_acronym "
                + "FROM sgpt.program p";
        stmt = conn.prepareStatement(sql);
        rs = stmt.executeQuery();

        while (rs.next()) {
            String programAcronym = rs.getString("program_acronym");
            programComboBox.getItems().add(programAcronym);
        }

    }

    private void fetchStudents(Connection conn, PreparedStatement stmt, ResultSet rs) throws SQLException {
        conn = DatabaseConnection.getConnection();

        String sql = "SELECT si.student_id, si.last_name, si.first_name, p.program_acronym "
                + "FROM students.student_information si "
                + "JOIN sgpt.program p ON si.program_id = p.id";
        stmt = conn.prepareStatement(sql);
        rs = stmt.executeQuery();

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
}
