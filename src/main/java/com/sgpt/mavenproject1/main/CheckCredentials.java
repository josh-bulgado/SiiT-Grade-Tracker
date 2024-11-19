
package com.sgpt.mavenproject1.main;

import java.sql.*;

public class CheckCredentials {

    public boolean checkCredentials(String id, String password, boolean admin) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();

            String sql = "SELECT * FROM auth.student_login WHERE student_id = ? AND password = ?";
            if (admin) {
                sql = "SELECT * FROM auth.admin_login WHERE username = ? AND password = ?";
            }

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            stmt.setString(2, password);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return true;
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
        return false;
    }

}
