package UI;

import Conn.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainMenu extends JFrame {
    private String username;
    private String userRole;

    public MainMenu(String username) {
        this.username = username;
        setTitle("CyberGames - Menu Principal");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Récupérer le rôle de l'utilisateur
        fetchUserRole();

        // Ajouter un hook de fermeture pour désactiver les forfaits actifs
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Début du shutdown hook pour l'utilisateur : " + username);
            deactivateAllActivePackages();
            System.out.println("Fin du shutdown hook.");
        }));

        // Création du panneau principal
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton myPackagesButton = new JButton("Mes Forfaits");
        myPackagesButton.addActionListener(e -> {
            new MyPackagesWindow(username).setVisible(true);
            dispose();
        });
        panel.add(myPackagesButton);

        JButton buyPackageButton = new JButton("Acheter un Forfait");
        buyPackageButton.addActionListener(e -> {
            new BuyPackageWindow(username).setVisible(true);
            dispose();
        });
        panel.add(buyPackageButton);

        JButton manageMachinesButton = new JButton("Gérer les Machines");
        manageMachinesButton.addActionListener(e -> {
            if ("admin".equals(userRole) || "manager".equals(userRole)) {
                new Machine.ManageMachinesWindow().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Accès réservé aux administrateurs et managers.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(manageMachinesButton);

        JButton quitButton = new JButton("Quitter");
        quitButton.addActionListener(e -> {
            System.out.println("Quitter cliqué, fermeture propre.");
            System.exit(0);
        });
        panel.add(quitButton);

        add(panel);
        setVisible(true);
    }

    private void fetchUserRole() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT role FROM users WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userRole = rs.getString("role");
                System.out.println("Rôle récupéré : " + userRole);
            } else {
                userRole = "user";
                System.out.println("Utilisateur non trouvé, rôle par défaut : " + userRole);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            userRole = "user";
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deactivateAllActivePackages() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Tentative de désactivation des forfaits actifs pour " + username);
            String query = "UPDATE user_packages up " +
                    "JOIN users u ON up.user_id = u.id " +
                    "SET up.is_active = 0, up.updated_at = NOW() " +
                    "WHERE u.name = ? AND up.is_active = 1";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println(rowsAffected + " forfait(s) actif(s) désactivé(s) pour l'utilisateur " + username);
            } else {
                System.out.println("Aucun forfait actif trouvé pour l'utilisateur " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la désactivation des forfaits : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String username = args.length > 0 ? args[0] : JOptionPane.showInputDialog(null, "Entrez votre nom d'utilisateur :");
            if (username != null && !username.trim().isEmpty()) {
                new MainMenu(username).setVisible(true);
            } else {
                System.out.println("Aucun nom d'utilisateur fourni. Fermeture.");
                System.exit(0);
            }
        });
    }
}