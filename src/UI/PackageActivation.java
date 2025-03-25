// PackageActivation.java
package UI;

import Conn.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class PackageActivation extends JDialog {
    private final int userId;
    private JTextField codeField;

    public PackageActivation(int userId, JFrame parent) {
        super(parent, "Activation de Forfait", true);
        this.userId = userId;
        initUI();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setSize(300, 150);
        setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Code d'activation :"));
        codeField = new JTextField();
        panel.add(codeField);

        JButton activateBtn = new JButton("Activer");
        activateBtn.addActionListener(e -> activatePackage());

        add(panel, BorderLayout.CENTER);
        add(activateBtn, BorderLayout.SOUTH);
    }

    private void activatePackage() {
        String code = codeField.getText().trim();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE user_packages SET is_active = 1, activation_date = NOW() " +
                             "WHERE user_id = ? AND code = ?")) {

            stmt.setInt(1, userId);
            stmt.setString(2, code);
            int updated = stmt.executeUpdate();

            if (updated > 0) {
                new PackageTimer(userId, code).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Code invalide ou forfait déjà activé",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur de base de données",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}