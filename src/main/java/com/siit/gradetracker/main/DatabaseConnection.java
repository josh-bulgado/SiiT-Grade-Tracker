package com.siit.gradetracker.main;

import java.sql.*;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://10.0.0.44:3306";
    private static final String USER = "root";
    private static final String PASSWORD = "joshua";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
