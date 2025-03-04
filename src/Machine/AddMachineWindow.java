package Machine;

import DAO.MachineDAO;
import Model.Machine;

import javax.swing.*;
import java.awt.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddMachineWindow extends JFrame {
    private JTextField nameField, processorField, memoryField, storageField, osField, statusField, purchaseDateField;

    public AddMachineWindow() {
        setTitle("Ajouter une Machine");
        setSize(400, 350);
        setLocationRelativeTo(null);

        nameField = new JTextField(20);
        processorField = new JTextField(20);
        memoryField = new JTextField(20);
        storageField = new JTextField(20);
        osField = new JTextField(20);
        statusField = new JTextField(20);
        purchaseDateField = new JTextField(20);

        JButton addButton = new JButton("Ajouter");
        addButton.addActionListener(e -> {
            try {
                Machine machine = new Machine();
                machine.setName(nameField.getText());
                machine.setProcessor(processorField.getText());
                machine.setMemory(Integer.parseInt(memoryField.getText()));
                machine.setStorage(Integer.parseInt(storageField.getText()));
                machine.setOperatingSystem(osField.getText());
                machine.setStatus(statusField.getText());

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = dateFormat.parse(purchaseDateField.getText());
                machine.setPurchaseDate(new java.sql.Date(date.getTime()));

                new MachineDAO().insert(machine);
                JOptionPane.showMessageDialog(this, "Machine ajoutée avec succès.");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
            }
        });

        JPanel panel = new JPanel(new GridLayout(8, 2));
        panel.add(new JLabel("Nom :")); panel.add(nameField);
        panel.add(new JLabel("Processeur :")); panel.add(processorField);
        panel.add(new JLabel("Mémoire (Go) :")); panel.add(memoryField);
        panel.add(new JLabel("Stockage (Go) :")); panel.add(storageField);
        panel.add(new JLabel("OS :")); panel.add(osField);
        panel.add(new JLabel("Statut :")); panel.add(statusField);
        panel.add(new JLabel("Date d'achat (yyyy-MM-dd) :")); panel.add(purchaseDateField);
        panel.add(addButton);

        add(panel);
    }
}