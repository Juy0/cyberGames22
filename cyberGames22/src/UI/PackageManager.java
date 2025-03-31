package UI;

import Conn.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class PackageManager extends JFrame {
    private final int userId;
    private JComboBox<String> packageCombo;

    public PackageManager(int userId, JFrame parent) {
        this.userId = userId;
        initUI();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setTitle("Achat de Forfait");
        setSize(400, 200);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        JLabel packageLabel = new JLabel("Choisir un forfait :");
        packageCombo = new JComboBox<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, name, duration, price FROM packages");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                packageCombo.addItem(String.format("%s - %d min (%.2f€)",
                        rs.getString("name"),
                        rs.getInt("duration"),
                        rs.getDouble("price")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JButton buyBtn = new JButton("Acheter");
        buyBtn.addActionListener(e -> purchasePackage());

        formPanel.add(packageLabel);
        formPanel.add(packageCombo);
        formPanel.add(new JLabel()); // Espace vide
        formPanel.add(buyBtn);

        add(formPanel, BorderLayout.CENTER);
    }

    private void purchasePackage() {
        String selected = (String) packageCombo.getSelectedItem();
        if (selected != null) {
            int packageId = packageCombo.getSelectedIndex() + 1;
            String code = generateUniqueCode();

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO user_packages (user_id, package_id, code) VALUES (?, ?, ?)")) {

                stmt.setInt(1, userId);
                stmt.setInt(2, packageId);
                stmt.setString(3, code);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Forfait acheté !\nVotre code d'activation : " + code,
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);

                dispose();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'achat",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String generateUniqueCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return code.toString();
    }
}