
package com.siit.main;

import java.sql.*;

public class CheckCredentials {

    public String checkCredentials(String email_address, String password) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try (Connection conn = DatabaseConnection.getConnection()) {

            String sql = "SELECT * FROM auth.admin_login WHERE email_address = ? AND password = ?";
            String role = "faculty";

            if (email_address.matches(".*\\d{6}.*")) {
                sql = "SELECT * FROM auth.student_login WHERE email_address = ? AND password = ?";
                role = "student";
            }

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email_address);
            stmt.setString(2, password);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return role;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "invalid";
    }

}
