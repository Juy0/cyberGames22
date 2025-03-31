package UI;

import Conn.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MyPackagesWindow extends JFrame {
    private String username;
    private JTable packagesTable;
    private DefaultTableModel tableModel;

    public MyPackagesWindow(String username) {
        this.username = username;
        setTitle("Mes Forfaits");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Création du panneau principal
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Création du tableau pour afficher les forfaits
        String[] columnNames = {"Code", "Forfait", "Temps Restant", "Statut"};
        tableModel = new DefaultTableModel(columnNames, 0);
        packagesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(packagesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Chargement des forfaits de l'utilisateur
        loadPackages();

        // Bouton "Activer le forfait"
        JButton activateButton = new JButton("Activer le forfait");
        activateButton.addActionListener(e -> {
            int selectedRow = packagesTable.getSelectedRow();
            if (selectedRow >= 0) {
                String code = (String) tableModel.getValueAt(selectedRow, 0);
                new ActivatePackageWindow(username, code).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un forfait à activer.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Bouton "Retour"
        JButton backButton = new JButton("Retour");
        backButton.addActionListener(e -> {
            new MainMenu(username).setVisible(true);
            dispose();
        });

        // Panneau pour les boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(activateButton);
        buttonPanel.add(backButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }

    // Charger les forfaits de l'utilisateur depuis la base de données
    private void loadPackages() {
        tableModel.setRowCount(0); // Vider le tableau
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT up.code, p.name, up.remaining_time, up.is_active " +
                    "FROM user_packages up " +
                    "JOIN packages p ON up.package_id = p.id " +
                    "JOIN users u ON up.user_id = u.id " +
                    "WHERE u.name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String code = rs.getString("code");
                String packageName = rs.getString("name");
                int remainingTime = rs.getInt("remaining_time");
                boolean isActive = rs.getBoolean("is_active");
                String status = isActive ? "Actif" : "Inactif";
                Object[] row = {code, packageName, remainingTime + " min", status};
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des forfaits.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}