package Conn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection conn;
    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            String url = "jdbc:mysql://localhost:3306/cybergamesArras2";
            String username = "your_username";
            String password = "your_password";
            conn = DriverManager.getConnection(url, username, password);
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/votre_base", "user", "password");
        }
        return conn;
    }
}