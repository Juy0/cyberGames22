package UI;

import Conn.DatabaseConnection;
import Machine.ManageMachinesWindow;
import Session.UserSession;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainWindow extends JFrame {
    private String userRole; // Pour stocker le rôle de l'utilisateur

    public MainWindow() {
        String user = UserSession.getInstance().getUsername();
        setTitle("Application Principale - Bienvenue " + user);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Récupérer le rôle de l'utilisateur
        fetchUserRole(user);

        // Création d'un panneau pour organiser les composants
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Bouton "Mes Forfaits"
        JButton myPackagesButton = new JButton("Mes Forfaits");
        myPackagesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        myPackagesButton.addActionListener(e -> {
            new MyPackagesWindow(user).setVisible(true);
            dispose();
        });
        panel.add(myPackagesButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Espacement

        // Bouton "Acheter un Forfait"
        JButton buyPackageButton = new JButton("Acheter un Forfait");
        buyPackageButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buyPackageButton.addActionListener(e -> {
            new BuyPackageWindow(user).setVisible(true);
            dispose();
        });
        panel.add(buyPackageButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Espacement

        // Bouton "Gérer les Machines" (visible pour admin/manager)
        JButton manageMachinesButton = new JButton("Gérer les Machines");
        manageMachinesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        manageMachinesButton.addActionListener(e -> {
            if (userRole.equals("admin") || userRole.equals("manager")) {
                new ManageMachinesWindow().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Accès réservé aux administrateurs et managers.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(manageMachinesButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Espacement

        // Bouton "Quitter"
        JButton quitButton = new JButton("Quitter");
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitButton.addActionListener(e -> System.exit(0));
        panel.add(quitButton);

        // Ajouter le panneau à la fenêtre
        add(panel);
        setVisible(true);
    }

    // Méthode pour récupérer le rôle de l'utilisateur
    private void fetchUserRole(String username) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT role FROM users WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userRole = rs.getString("role");
            } else {
                userRole = "user"; // Par défaut
            }
        } catch (SQLException e) {
            e.printStackTrace();
            userRole = "user"; // En cas d'erreur
        }
    }
}