// PackageTimer.java
package UI;

import Conn.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class PackageTimer extends JFrame {
    private final int userId;
    private final String code;
    private int remainingTime;
    private Timer swingTimer;
    private JLabel timeLabel;
    private JButton pauseButton;
    private boolean isPaused = false;

    public PackageTimer(int userId, String code) {
        this.userId = userId;
        this.code = code;
        loadPackageData();
        initUI();
        startTimer();
    }

    private void loadPackageData() {
        String query = "SELECT remaining_time FROM user_packages WHERE user_id = ? AND code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setString(2, code);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                remainingTime = rs.getInt("remaining_time");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        setTitle("Décompte du Forfait");
        setSize(300, 150);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        timeLabel = new JLabel(formatTime(remainingTime), SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 24));

        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(this::togglePause);

        panel.add(timeLabel);
        panel.add(pauseButton);

        add(panel, BorderLayout.CENTER);
    }

    private void startTimer() {
        swingTimer = new Timer(1000, e -> {
            if (!isPaused && remainingTime > 0) {
                remainingTime--;
                updateTimeDisplay();
                updateDatabase();

                if (remainingTime <= 0) {
                    swingTimer.stop();
                    JOptionPane.showMessageDialog(this, "Temps écoulé !");
                    dispose();
                }
            }
        });
        swingTimer.start();
    }

    private void togglePause(ActionEvent e) {
        isPaused = !isPaused;
        pauseButton.setText(isPaused ? "Reprendre" : "Pause");

        if (!isPaused) {
            swingTimer.restart();
        }
    }

    private String formatTime(int seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    private void updateTimeDisplay() {
        timeLabel.setText(formatTime(remainingTime));
    }

    private void updateDatabase() {
        String query = "UPDATE user_packages SET remaining_time = ? WHERE user_id = ? AND code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, remainingTime);
            stmt.setInt(2, userId);
            stmt.setString(3, code);
            stmt.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        if (swingTimer != null) {
            swingTimer.stop();
        }
        super.dispose();
    }
}