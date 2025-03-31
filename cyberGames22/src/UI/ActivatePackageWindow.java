package UI;

import Conn.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ActivatePackageWindow extends JFrame {
    private String username;
    private String code;

    public ActivatePackageWindow(String username, String code) {
        this.username = username;
        this.code = code;
        setTitle("Activation de Forfait");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Création du panneau principal
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Champ pour entrer le code d'activation
        JLabel label = new JLabel("Code d'activation :");
        JTextField codeField = new JTextField(code);
        codeField.setEditable(false); // Le code est pré-rempli et non modifiable

        // Bouton "Activer"
        JButton activateButton = new JButton("Activer");
        activateButton.addActionListener(e -> activatePackage());

        panel.add(label);
        panel.add(codeField);
        panel.add(activateButton);

        add(panel);
        setVisible(true);
    }

    // Activer le forfait
    private void activatePackage() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Vérifier l'état actuel du forfait
            String checkQuery = "SELECT remaining_time, is_active FROM user_packages up " +
                    "JOIN users u ON up.user_id = u.id " +
                    "WHERE u.name = ? AND up.code = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            checkStmt.setString(2, code);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int remainingTime = rs.getInt("remaining_time");
                boolean isActive = rs.getBoolean("is_active");
                System.out.println("État du forfait : code=" + code + ", remaining_time=" + remainingTime + ", is_active=" + isActive);

                if (!isActive) { // Si le forfait est inactif
                    if (remainingTime > 0) {
                        // Activer le forfait
                        String updateQuery = "UPDATE user_packages up " +
                                "JOIN users u ON up.user_id = u.id " +
                                "SET up.is_active = 1, up.activation_date = NOW() " +
                                "WHERE u.name = ? AND up.code = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                        updateStmt.setString(1, username);
                        updateStmt.setString(2, code);
                        int rowsAffected = updateStmt.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(this, "Forfait activé avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                            new TimerWindow(username, code).setVisible(true);
                            dispose(); // Ferme la fenêtre actuelle
                        } else {
                            System.out.println("Échec de l'activation du forfait pour code=" + code);
                            JOptionPane.showMessageDialog(this, "Échec de l'activation du forfait.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Ce forfait est épuisé (temps restant = 0).", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Le forfait est déjà actif.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Code invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                System.out.println("Aucun forfait trouvé pour username=" + username + ", code=" + code);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'activation du forfait : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}