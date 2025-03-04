package UI;

import Machine.ManageMachinesWindow;
import User.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame {
    private User user; // L'utilisateur connecté

    public MainWindow(User user) {
        this.user = user;
        setTitle("Application Principale - Bienvenue " + user.getName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Création d'un panneau pour organiser les composants
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Boutons selon le rôle de l'utilisateur
        if (user.getRole().equals("admin")) {
            JButton manageUsersButton = new JButton("Gestion des Utilisateurs");
            manageUsersButton.addActionListener(e -> {
                new ManageUsersWindow().setVisible(true); // Ouvre une fenêtre de gestion des utilisateurs
            });
            panel.add(manageUsersButton);
        }

        if (user.getRole().equals("admin") || user.getRole().equals("manager")) {
            JButton manageMachinesButton = new JButton("Gestion des Machines");
            manageMachinesButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new ManageMachinesWindow().setVisible(true);
                }
            });
            panel.add(manageMachinesButton);
        }

        // Ajoutez le panneau à la fenêtre
        add(panel);
    }
}