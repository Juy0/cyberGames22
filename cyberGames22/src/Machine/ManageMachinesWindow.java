package Machine;

import Conn.DatabaseConnection;
import UI.MainMenu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageMachinesWindow extends JFrame {
    private JTable machinesTable;
    private DefaultTableModel tableModel;

    public ManageMachinesWindow() {
        setTitle("Gérer les Machines");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Création du panneau principal
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Création du tableau pour afficher les machines
        String[] columnNames = {"ID", "Nom", "Processeur", "Mémoire", "Stockage", "Système d'exploitation", "Statut"};
        tableModel = new DefaultTableModel(columnNames, 0);
        machinesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(machinesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Chargement des données des machines
        loadMachines();

        // Création du panneau pour les boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Ajouter");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");
        JButton backButton = new JButton("Retour");

        addButton.addActionListener(e -> new AddMachineWindow(this).setVisible(true));
        editButton.addActionListener(e -> {
            int selectedRow = machinesTable.getSelectedRow();
            if (selectedRow >= 0) {
                int machineId = (int) tableModel.getValueAt(selectedRow, 0);
                new EditMachineWindow(this, machineId).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une machine à modifier.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = machinesTable.getSelectedRow();
            if (selectedRow >= 0) {
                int machineId = (int) tableModel.getValueAt(selectedRow, 0);
                deleteMachine(machineId);
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une machine à supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        backButton.addActionListener(e -> {
            // Retour à MainMenu (on passe un nom d'utilisateur fictif, à ajuster selon ton implémentation)
            new MainMenu("admin").setVisible(true); // Remplace "admin" par le nom d'utilisateur actuel si nécessaire
            dispose();
        });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }

    // Charger les machines depuis la base de données
    public void loadMachines() {
        tableModel.setRowCount(0); // Vider le tableau
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM machines";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("processor"),
                        rs.getInt("memory"),
                        rs.getInt("storage"),
                        rs.getString("operating_system"),
                        rs.getString("status")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des machines.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Supprimer une machine
    private void deleteMachine(int machineId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM machines WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, machineId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Machine supprimée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                loadMachines(); // Rafraîchir le tableau
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de la machine.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}