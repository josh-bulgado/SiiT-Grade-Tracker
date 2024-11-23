
package com.sgpt.mavenproject1.main;

import java.sql.*;

public class CheckCredentials {

    public String checkCredentials(String email_address, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();

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
        } finally {
            // Close resources to avoid memory leaks
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "invalid";
    }

}
