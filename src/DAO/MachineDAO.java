package DAO;

import Conn.DatabaseConnection;
import Model.Machine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MachineDAO {
    public void insert(Machine machine) throws SQLException {
        String sql = "INSERT INTO machines (name, processor, memory, storage, operating_system, purchase_date, status, games_installed, last_maintenance_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, machine.getName());
            stmt.setString(2, machine.getProcessor());
            stmt.setInt(3, machine.getMemory());
            stmt.setInt(4, machine.getStorage());
            stmt.setString(5, machine.getOperatingSystem());

            // Conversion de purchaseDate en java.sql.Date
            java.sql.Date purchaseDate;
            if (machine.getPurchaseDate() == null) {
                purchaseDate = new java.sql.Date(System.currentTimeMillis());
            } else {
                purchaseDate = new java.sql.Date(machine.getPurchaseDate().getTime());
            }
            stmt.setDate(6, purchaseDate);

            // Gestion de status avec valeur par défaut
            stmt.setString(7, machine.getStatus() != null ? machine.getStatus() : "disponible");

            // games_installed peut être null
            stmt.setString(8, machine.getGamesInstalled());

            // Conversion de lastMaintenanceDate en java.sql.Date, peut être null
            java.sql.Date lastMaintenanceDate = machine.getLastMaintenanceDate() != null ?
                    new java.sql.Date(machine.getLastMaintenanceDate().getTime()) : null;
            stmt.setDate(9, lastMaintenanceDate);

            stmt.executeUpdate();
        }
    }


    public Machine getById(long id) throws SQLException {
        Machine machine = null;
        String sql = "SELECT * FROM machines WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    machine = new Machine();
                    machine.setId(rs.getLong("id"));
                    machine.setName(rs.getString("name"));
                    machine.setProcessor(rs.getString("processor"));
                    machine.setMemory(rs.getInt("memory"));
                    machine.setStorage(rs.getInt("storage"));
                    machine.setOperatingSystem(rs.getString("operating_system"));
                    machine.setGamesInstalled(rs.getString("games_installed"));
                    machine.setPurchaseDate(rs.getDate("purchase_date"));
                    machine.setLastMaintenanceDate(rs.getDate("last_maintenance_date"));
                    machine.setStatus(rs.getString("status"));
                    Long userId = rs.getLong("user_id");
                    if (rs.wasNull()) {
                        userId = null;
                    }
                    machine.setUserId(userId);
                }
            }
        }
        return machine;
    }


    public void update(Machine machine) throws SQLException {
        String sql = "UPDATE machines SET name = ?, processor = ?, memory = ?, storage = ?, operating_system = ?, games_installed = ?, purchase_date = ?, last_maintenance_date = ?, status = ?, user_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, machine.getName());
            stmt.setString(2, machine.getProcessor());
            stmt.setInt(3, machine.getMemory());
            stmt.setInt(4, machine.getStorage());
            stmt.setString(5, machine.getOperatingSystem());
            stmt.setString(6, machine.getGamesInstalled());
            java.sql.Date purchaseDate = machine.getPurchaseDate() != null ?
                    new java.sql.Date(machine.getPurchaseDate().getTime()) : null;
            stmt.setDate(7, purchaseDate);
            java.sql.Date lastMaintenanceDate = machine.getLastMaintenanceDate() != null ?
                    new java.sql.Date(machine.getLastMaintenanceDate().getTime()) : null;
            stmt.setDate(8, lastMaintenanceDate);
            stmt.setString(9, machine.getStatus());
            if (machine.getUserId() == null) {
                stmt.setNull(10, Types.BIGINT);
            } else {
                stmt.setLong(10, machine.getUserId());
            }
            stmt.setLong(11, machine.getId());
            stmt.executeUpdate();
        }
    }


    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM machines WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Machine> getAll() throws SQLException {
        List<Machine> machines = new ArrayList<>();
        String sql = "SELECT * FROM machines";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Machine machine = new Machine();
                machine.setId(rs.getLong("id"));
                machine.setName(rs.getString("name"));
                machine.setProcessor(rs.getString("processor"));
                machine.setMemory(rs.getInt("memory"));
                machine.setStorage(rs.getInt("storage"));
                machine.setOperatingSystem(rs.getString("operating_system"));
                machine.setGamesInstalled(rs.getString("games_installed"));
                machine.setPurchaseDate(rs.getDate("purchase_date"));
                machine.setLastMaintenanceDate(rs.getDate("last_maintenance_date"));
                machine.setStatus(rs.getString("status"));
                Long userId = rs.getLong("user_id");
                if (rs.wasNull()) {
                    machine.setUserId(null);
                } else {
                    machine.setUserId(userId);
                }
                machines.add(machine);
            }
        }
        return machines;
    }
}