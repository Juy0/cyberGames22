package Machine;

import Conn.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddMachineWindow extends JFrame {
    private ManageMachinesWindow parent;
    private JTextField nameField, processorField, memoryField, storageField, osField;
    private JComboBox<String> statusComboBox;

    public AddMachineWindow(ManageMachinesWindow parent) {
        this.parent = parent;
        setTitle("Ajouter une Machine");
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

        // Bouton "Ajouter"
        JButton addButton = new JButton("Ajouter");
        addButton.addActionListener(e -> addMachine());

        // Bouton "Annuler"
        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(e -> dispose());

        panel.add(addButton);
        panel.add(cancelButton);

        add(panel);
        setVisible(true);
    }

    // Ajouter une machine dans la base de données
    private void addMachine() {
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
            // Ajouter purchase_date avec la date actuelle (CURRENT_DATE)
            String query = "INSERT INTO machines (name, processor, memory, storage, operating_system, status, purchase_date) VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, processor);
            stmt.setInt(3, memory);
            stmt.setInt(4, storage);
            stmt.setString(5, os);
            stmt.setString(6, status);
            // Pas besoin de set pour purchase_date, car on utilise CURRENT_DATE directement dans la requête
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Machine ajoutée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                parent.loadMachines(); // Rafraîchir la liste des machines
                dispose();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de la machine : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}