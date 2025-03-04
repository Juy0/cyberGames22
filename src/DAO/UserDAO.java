package DAO;

import Conn.DatabaseConnection;
import User.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public User getByEmail(String email) throws SQLException {
        User user = null;
        String sql = "SELECT * FROM users WHERE email = ?"; // Requête SQL

        try (Connection conn = DatabaseConnection.getConnection(); // Connexion à la base
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email); // Remplace le ? par l'email
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) { // Si un utilisateur est trouvé
                    user = new User();
                    user.setId(rs.getLong("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password")); // Mot de passe haché
                    user.setRole(rs.getString("role"));

                }
            }
        }
        return user; // Retourne l'utilisateur ou null si non trouvé
    }
}