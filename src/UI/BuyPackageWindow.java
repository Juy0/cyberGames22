package UI;

import Conn.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class BuyPackageWindow extends JFrame {
    private String username;
    private JComboBox<String> packageComboBox;
    private DefaultComboBoxModel<String> packageModel;

    public BuyPackageWindow(String username) {
        this.username = username;
        setTitle("Achat de Forfait");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Création du panneau principal
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Liste déroulante pour choisir un forfait
        JLabel label = new JLabel("Choisir un forfait :");
        packageModel = new DefaultComboBoxModel<>();
        packageComboBox = new JComboBox<>(packageModel);
        loadPackages();

        // Bouton "Acheter"
        JButton buyButton = new JButton("Acheter");
        buyButton.addActionListener(e -> buyPackage());

        panel.add(label);
        panel.add(packageComboBox);
        panel.add(buyButton);

        add(panel);
        setVisible(true);
    }

    // Charger les forfaits disponibles depuis la base de données
    private void loadPackages() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id, name, duration FROM packages";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int duration = rs.getInt("duration");
                packageModel.addElement(name + " - " + duration + " min (" + id + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des forfaits.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Acheter un forfait
    private void buyPackage() {
        String selectedPackage = (String) packageComboBox.getSelectedItem();
        if (selectedPackage == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un forfait.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Extraire l'ID du forfait à partir de la chaîne sélectionnée
        String packageIdStr = selectedPackage.substring(selectedPackage.lastIndexOf("(") + 1, selectedPackage.lastIndexOf(")"));
        int packageId = Integer.parseInt(packageIdStr);

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Récupérer l'ID de l'utilisateur
            String userQuery = "SELECT id FROM users WHERE name = ?";
            PreparedStatement userStmt = conn.prepareStatement(userQuery);
            userStmt.setString(1, username);
            ResultSet userRs = userStmt.executeQuery();
            if (!userRs.next()) {
                JOptionPane.showMessageDialog(this, "Utilisateur non trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int userId = userRs.getInt("id");

            // Récupérer la durée du forfait
            String packageQuery = "SELECT duration FROM packages WHERE id = ?";
            PreparedStatement packageStmt = conn.prepareStatement(packageQuery);
            packageStmt.setInt(1, packageId);
            ResultSet packageRs = packageStmt.executeQuery();
            if (!packageRs.next()) {
                JOptionPane.showMessageDialog(this, "Forfait non trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int duration = packageRs.getInt("duration");

            // Générer un code unique
            String code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            // Insérer le forfait dans user_packages
            String insertQuery = "INSERT INTO user_packages (user_id, package_id, code, remaining_time, is_active, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, 0, NOW(), NOW())";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, packageId);
            insertStmt.setString(3, code);
            insertStmt.setInt(4, duration * 60); // Convertir en secondes
            int rowsAffected = insertStmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Forfait acheté ! Votre code d'activation : " + code, "Succès", JOptionPane.INFORMATION_MESSAGE);
                new MainMenu(username).setVisible(true);
                dispose();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'achat du forfait.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}