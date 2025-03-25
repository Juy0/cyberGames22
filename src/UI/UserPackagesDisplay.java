// UserPackagesDisplay.java
package UI;

import Conn.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UserPackagesDisplay extends JFrame {
    private final int userId;

    public UserPackagesDisplay(int userId) throws SQLException {
        this.userId = userId;
        initUI();
    }

    private void initUI() {
        setTitle("Mes Forfaits");
        setSize(600, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Code");
        model.addColumn("Forfait");
        model.addColumn("Temps Restant");
        model.addColumn("Statut");

        String query = "SELECT up.code, p.name, up.remaining_time, up.is_active "
                + "FROM user_packages up "
                + "JOIN packages p ON up.package_id = p.id "
                + "WHERE up.user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getInt("remaining_time") + " min",
                        rs.getBoolean("is_active") ? "Actif" : "Inactif"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        JButton activateBtn = new JButton("Activer le forfait");
        activateBtn.addActionListener(e -> new PackageActivation(userId, this).setVisible(true));
        add(activateBtn, BorderLayout.SOUTH);
    }
}