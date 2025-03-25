package UI;

import Conn.DatabaseConnection;
import Machine.ManageMachinesWindow;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainMenu extends JFrame {
    private String username;
    private String userRole; // Ajout pour stocker le rôle de l'utilisateur

    public MainMenu(String username) {
        this.username = username;
        setTitle("CyberGames - Menu Principal");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Récupérer le rôle de l'utilisateur depuis la base de données
        fetchUserRole();

        // Création du panneau principal
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10)); // Ajout d'une ligne pour le nouveau bouton
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Bouton "Mes Forfaits"
        JButton myPackagesButton = new JButton("Mes Forfaits");
        myPackagesButton.addActionListener(e -> {
            new MyPackagesWindow(username).setVisible(true);
            dispose();
        });
        panel.add(myPackagesButton);

        // Bouton "Acheter un Forfait"
        JButton buyPackageButton = new JButton("Acheter un Forfait");
        buyPackageButton.addActionListener(e -> {
            new BuyPackageWindow(username).setVisible(true);
            dispose();
        });
        panel.add(buyPackageButton);

        // Bouton "Gérer les Machines" (visible uniquement pour admin/manager)
        JButton manageMachinesButton = new JButton("Gérer les Machines");
        manageMachinesButton.addActionListener(e -> {
            if (userRole.equals("admin") || userRole.equals("manager")) {
                new ManageMachinesWindow().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Accès réservé aux administrateurs et managers.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(manageMachinesButton);

        // Bouton "Quitter"
        JButton quitButton = new JButton("Quitter");
        quitButton.addActionListener(e -> System.exit(0));
        panel.add(quitButton);

        add(panel);
    }

    // Méthode pour récupérer le rôle de l'utilisateur depuis la base de données
    private void fetchUserRole() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT role FROM users WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userRole = rs.getString("role");
            } else {
                userRole = "user"; // Par défaut, si l'utilisateur n'est pas trouvé
            }
        } catch (SQLException e) {
            e.printStackTrace();
            userRole = "user"; // En cas d'erreur, on définit un rôle par défaut
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String username = JOptionPane.showInputDialog(null, "Entrez votre nom d'utilisateur :");
            if (username != null && !username.trim().isEmpty()) {
                new MainMenu(username).setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}