package Machine;

import DAO.MachineDAO;
import Model.Machine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class ManageMachinesWindow extends JFrame {
    private JTable machinesTable;
    private JButton addButton, editButton, deleteButton;
    private MachineDAO machineDAO;

    public ManageMachinesWindow() {
        // Configuration de la fenêtre
        setTitle("Gestion des Machines");
        setSize(800, 600);
        setLocationRelativeTo(null); // Centrer la fenêtre
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Ferme cette fenêtre sans quitter l’app

        // Initialisation du DAO
        machineDAO = new MachineDAO();

        // Création du tableau des machines
        machinesTable = createMachinesTable();

        // Création des boutons
        addButton = new JButton("Ajouter");
        editButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");

        // Action du bouton "Ajouter"
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddMachineWindow().setVisible(true); // Ouvre une fenêtre pour ajouter une machine
            }
        });

        // Action du bouton "Modifier"
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = machinesTable.getSelectedRow();
                if (selectedRow != -1) {
                    long machineId = (long) machinesTable.getValueAt(selectedRow, 0); // Suppose que l'ID est dans la colonne 0
                    EditMachineWindow editWindow = new EditMachineWindow(machineId);
                    editWindow.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(ManageMachinesWindow.this, "Veuillez sélectionner une machine à modifier.");
                }
            }
        });

        // Action du bouton "Supprimer"
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = machinesTable.getSelectedRow();
                if (selectedRow != -1) {
                    long machineId = (long) machinesTable.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(ManageMachinesWindow.this,
                            "Êtes-vous sûr de vouloir supprimer cette machine ?",
                            "Confirmation", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            machineDAO.delete(machineId);
                            refreshTable(); // Rafraîchit le tableau
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(ManageMachinesWindow.this, "Erreur lors de la suppression.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(ManageMachinesWindow.this, "Veuillez sélectionner une machine à supprimer.");
                }
            }
        });

        // Organisation des composants
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(new JScrollPane(machinesTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JTable createMachinesTable() {
        String[] columns = {"ID", "Nom", "Processeur", "Mémoire", "Stockage", "OS", "Statut"};
        Object[][] data = getMachinesData();
        return new JTable(data, columns);
    }

    private Object[][] getMachinesData() {
        try {
            List<Machine> machines = machineDAO.getAll();
            Object[][] data = new Object[machines.size()][7];
            for (int i = 0; i < machines.size(); i++) {
                Machine m = machines.get(i);
                data[i][0] = m.getId();
                data[i][1] = m.getName();
                data[i][2] = m.getProcessor();
                data[i][3] = m.getMemory();
                data[i][4] = m.getStorage();
                data[i][5] = m.getOperatingSystem();
                data[i][6] = m.getStatus();
            }
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
            return new Object[0][7];
        }
    }

    private void refreshTable() {
        machinesTable.setModel(createMachinesTable().getModel());
    }
}