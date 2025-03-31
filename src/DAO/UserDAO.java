package DAO;

import Conn.DatabaseConnection;
import Session.UserSession;
import User.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public static boolean authenticate(String username, String password) {
        String sql = "SELECT id, password, role FROM users WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             stmt.setString(1, username);
             try (ResultSet rs = stmt.executeQuery()) {
                 if (rs.next()) {
                     String userId = rs.getString("id");
                     String passwordHash = rs.getString("password");
                     String role = rs.getString("role");

                     if (passwordHash.startsWith("$2y$")){
                         passwordHash = "$2a$" + passwordHash.substring(4);
                     }
                     if (BCrypt.checkpw(password, passwordHash)) {
                         UserSession.getInstance().setUserId(userId);
                         UserSession.getInstance().setUsername(username);

                         return true;
                     }
                 }
             } catch (SQLException e) {
                 e.printStackTrace();
             }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;

    }
}


