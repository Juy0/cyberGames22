package Machine;

import DAO.MachineDAO;
import Model.Machine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class EditMachineWindow extends JFrame {
    private long machineId;
    private MachineDAO machineDAO;
    private Machine machine;

    private JTextField nameField, processorField, memoryField, storageField, osField, statusField;

    public EditMachineWindow(long machineId) {
        this.machineId = machineId;
        this.machineDAO = new MachineDAO();

        // Récupérer la machine depuis la base de données
        try {
            machine = machineDAO.getById(machineId);
            if (machine == null) {
                JOptionPane.showMessageDialog(this, "Machine non trouvée.");
                dispose(); // Ferme la fenêtre si la machine n'existe pas
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération de la machine.");
            dispose();
            return;
        }

        // Configuration de la fenêtre
        setTitle("Modifier la Machine");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Champs de texte pré-remplis avec les données actuelles
        nameField = new JTextField(machine.getName(), 20);
        processorField = new JTextField(machine.getProcessor(), 20);
        memoryField = new JTextField(String.valueOf(machine.getMemory()), 20);
        storageField = new JTextField(String.valueOf(machine.getStorage()), 20);
        osField = new JTextField(machine.getOperatingSystem(), 20);
        statusField = new JTextField(machine.getStatus(), 20);

        // Bouton "Sauvegarder"
        JButton saveButton = new JButton("Sauvegarder");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Mettre à jour l'objet Machine avec les nouvelles valeurs
                machine.setName(nameField.getText());
                machine.setProcessor(processorField.getText());
                machine.setMemory(Integer.parseInt(memoryField.getText()));
                machine.setStorage(Integer.parseInt(storageField.getText()));
                machine.setOperatingSystem(osField.getText());
                machine.setStatus(statusField.getText());

                // Sauvegarder dans la base de données
                try {
                    machineDAO.update(machine);
                    JOptionPane.showMessageDialog(EditMachineWindow.this, "Machine modifiée avec succès.");
                    dispose(); // Ferme la fenêtre après sauvegarde
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(EditMachineWindow.this, "Erreur lors de la modification.");
                }
            }
        });

        // Mise en page avec un panneau
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.add(new JLabel("Nom :")); panel.add(nameField);
        panel.add(new JLabel("Processeur :")); panel.add(processorField);
        panel.add(new JLabel("Mémoire (Go) :")); panel.add(memoryField);
        panel.add(new JLabel("Stockage (Go) :")); panel.add(storageField);
        panel.add(new JLabel("Système d'exploitation :")); panel.add(osField);
        panel.add(new JLabel("Statut :")); panel.add(statusField);
        panel.add(saveButton);

        add(panel);
    }
}