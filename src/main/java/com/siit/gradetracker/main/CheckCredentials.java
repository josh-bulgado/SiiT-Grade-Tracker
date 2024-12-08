
package com.siit.gradetracker.main;

import java.sql.*;

public class CheckCredentials {

    public String checkCredentials(String email_address, String password)
            throws EmailNotFoundException, IncorrectPasswordException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String role = email_address.matches(".*\\d{6}.*") ? "student" : "faculty";
            String table = role.equals("student") ? "auth.student_login" : "auth.admin_login";

            // Check if email exists
            String sql = "SELECT password FROM " + table + " WHERE email_address = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email_address);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new EmailNotFoundException("Email not found");
                    }

                    // Check if password matches
                    if (!rs.getString("password").equals(password)) {
                        throw new IncorrectPasswordException("Incorrect password");
                    }
                }
            }

            return role;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "invalid";
    }

    public static class EmailNotFoundException extends Exception {
        public EmailNotFoundException(String message) {
            super(message);
        }
    }

    public static class IncorrectPasswordException extends Exception {
        public IncorrectPasswordException(String message) {
            super(message);
        }
    }

}
