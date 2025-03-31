package UI;

import Conn.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class TimerWindow extends JFrame {
    private String username;
    private String code;
    private JLabel timerLabel;
    private Timer timer;
    private boolean isPaused;
    private int remainingTime; // En secondes

    public TimerWindow(String username, String code) {
        this.username = username;
        this.code = code;
        this.isPaused = false;
        setTitle("Décompte du Forfait");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Création du panneau principal
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Label pour afficher le temps restant
        timerLabel = new JLabel("00:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Bouton "Pause"
        JButton pauseButton = new JButton("Pause");
        pauseButton.addActionListener(e -> togglePause());

        panel.add(timerLabel);
        panel.add(pauseButton);

        add(panel);
        setVisible(true);

        // Charger le temps restant et démarrer le décompte
        loadRemainingTime();
        startTimer();
    }

    // Charger le temps restant depuis la base de données
    private void loadRemainingTime() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT remaining_time FROM user_packages up " +
                    "JOIN users u ON up.user_id = u.id " +
                    "WHERE u.name = ? AND up.code = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                remainingTime = rs.getInt("remaining_time");
                updateTimerLabel();
                System.out.println("Temps restant chargé : " + remainingTime + " secondes");
            } else {
                System.out.println("Aucun forfait trouvé pour username=" + username + ", code=" + code);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement du temps restant : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Démarrer le décompte
    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isPaused && remainingTime > 0) {
                    remainingTime--;
                    updateTimerLabel();
                    updateRemainingTimeInDatabase();
                    if (remainingTime <= 0) {
                        timer.cancel();
                        deactivatePackage();
                        JOptionPane.showMessageDialog(TimerWindow.this, "Temps écoulé !", "Fin", JOptionPane.INFORMATION_MESSAGE);
                        new MainMenu(username).setVisible(true);
                        dispose();
                    }
                }
            }
        }, 1000, 1000); // Toutes les secondes
    }

    // Mettre à jour l'affichage du temps
    private void updateTimerLabel() {
        int minutes = remainingTime / 60;
        int seconds = remainingTime % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    // Mettre à jour le temps restant dans la base de données
    private void updateRemainingTimeInDatabase() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE user_packages up " +
                    "JOIN users u ON up.user_id = u.id " +
                    "SET up.remaining_time = ?, up.updated_at = NOW() " +
                    "WHERE u.name = ? AND up.code = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, remainingTime);
            stmt.setString(2, username);
            stmt.setString(3, code);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Temps restant mis à jour : " + remainingTime + " secondes");
            } else {
                System.out.println("Échec de la mise à jour du temps restant pour code=" + code);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la mise à jour du temps restant : " + e.getMessage());
        }
    }

    // Basculer entre pause et reprise
    private void togglePause() {
        isPaused = !isPaused;
        ((JButton) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).setText(isPaused ? "Reprendre" : "Pause");
        System.out.println("Décompte " + (isPaused ? "mis en pause" : "repris"));
    }

    // Désactiver le forfait dans la base de données
    private void deactivatePackage() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE user_packages up " +
                    "JOIN users u ON up.user_id = u.id " +
                    "SET up.is_active = 0, up.updated_at = NOW() " +
                    "WHERE u.name = ? AND up.code = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, code);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Forfait désactivé avec succès pour code=" + code);
            } else {
                System.out.println("Échec de la désactivation du forfait pour code=" + code);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la désactivation du forfait : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Surcharge de dispose pour désactiver le forfait dans tous les cas
    @Override
    public void dispose() {
        if (timer != null) {
            timer.cancel(); // Arrêter le timer
        }
        deactivatePackage(); // Désactiver le forfait systématiquement
        System.out.println("Fenêtre TimerWindow fermée, forfait désactivé");
        super.dispose();
    }
}