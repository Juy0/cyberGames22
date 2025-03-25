package Machine;

import Conn.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditMachineWindow extends JFrame {
    private ManageMachinesWindow parent;
    private int machineId;
    private JTextField nameField, processorField, memoryField, storageField, osField;
    private JComboBox<String> statusComboBox;

    public EditMachineWindow(ManageMachinesWindow parent, int machineId) {
        this.parent = parent;
        this.machineId = machineId;
        setTitle("Modifier une Machine");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Création du panneau principal
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Champs pour les informations de la machine
        panel.add(new JLabel("Nom :"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Processeur :"));
        processorField = new JTextField();
        panel.add(processorField);

        panel.add(new JLabel("Mémoire (Go) :"));
        memoryField = new JTextField();
        panel.add(memoryField);

        panel.add(new JLabel("Stockage (Go) :"));
        storageField = new JTextField();
        panel.add(storageField);

        panel.add(new JLabel("Système d'exploitation :"));
        osField = new JTextField();
        panel.add(osField);

        // Liste déroulante pour le statut
        panel.add(new JLabel("Statut :"));
        String[] statusOptions = {"Disponible", "En maintenance", "Hors service", "Réservé"};
        statusComboBox = new JComboBox<>(statusOptions);
        panel.add(statusComboBox);

        // Bouton "Modifier"
        JButton editButton = new JButton("Modifier");
        editButton.addActionListener(e -> editMachine());

        // Bouton "Annuler"
        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(e -> dispose());

        panel.add(editButton);
        panel.add(cancelButton);

        add(panel);

        // Charger les données de la machine
        loadMachineData();
        setVisible(true);
    }

    // Charger les données de la machine depuis la base de données
    private void loadMachineData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM machines WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, machineId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                processorField.setText(rs.getString("processor"));
                memoryField.setText(String.valueOf(rs.getInt("memory")));
                storageField.setText(String.valueOf(rs.getInt("storage")));
                osField.setText(rs.getString("operating_system"));
                statusComboBox.setSelectedItem(rs.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des données de la machine.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Modifier la machine dans la base de données
    private void editMachine() {
        String name = nameField.getText();
        String processor = processorField.getText();
        String memoryStr = memoryField.getText();
        String storageStr = storageField.getText();
        String os = osField.getText();
        String status = (String) statusComboBox.getSelectedItem();

        // Validation des champs
        if (name.isEmpty() || processor.isEmpty() || memoryStr.isEmpty() || storageStr.isEmpty() || os.isEmpty() || status.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int memory, storage;
        try {
            memory = Integer.parseInt(memoryStr);
            storage = Integer.parseInt(storageStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mémoire et stockage doivent être des nombres entiers.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Ne pas modifier purchase_date, donc on ne l'inclut pas dans la requête UPDATE
            String query = "UPDATE machines SET name = ?, processor = ?, memory = ?, storage = ?, operating_system = ?, status = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, processor);
            stmt.setInt(3, memory);
            stmt.setInt(4, storage);
            stmt.setString(5, os);
            stmt.setString(6, status);
            stmt.setInt(7, machineId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Machine modifiée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                parent.loadMachines(); // Rafraîchir la liste des machines
                dispose();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification de la machine : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}