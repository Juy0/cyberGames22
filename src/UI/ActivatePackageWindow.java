package UI;

import Conn.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
            String query = "UPDATE user_packages up " +
                    "JOIN users u ON up.user_id = u.id " +
                    "SET up.is_active = 1, up.activation_date = NOW() " +
                    "WHERE u.name = ? AND up.code = ? AND up.is_active = 0";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, code);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Forfait activé avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                new TimerWindow(username, code).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Code invalide ou forfait déjà activé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'activation du forfait.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}