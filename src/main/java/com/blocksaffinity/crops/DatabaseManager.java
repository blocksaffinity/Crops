package com.blocksaffinity.crops;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.sql.*;
import java.util.UUID;

public class DatabaseManager {
    private final JavaPlugin plugin;
    private Connection connection;

    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) dataFolder.mkdirs();

            // Connects to a local SQLite file 'data.db' inside your plugin folder
            String url = "jdbc:sqlite:" + new File(dataFolder, "data.db").getAbsolutePath();
            connection = DriverManager.getConnection(url);

            // Create the table if it doesn't exist
            try (Statement statement = connection.createStatement()) {
                statement.execute("""
                    CREATE TABLE IF NOT EXISTS player_crops (
                        uuid TEXT NOT NULL,
                        world TEXT NOT NULL,
                        x INTEGER NOT NULL,
                        y INTEGER NOT NULL,
                        z INTEGER NOT NULL,
                        PRIMARY KEY (world, x, y, z)
                    );
                """);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to connect to SQLite database!");
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add a crop location asynchronously
    public void addCrop(UUID uuid, Location loc) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "INSERT OR REPLACE INTO player_crops (uuid, world, x, y, z) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, uuid.toString());
                pstmt.setString(2, loc.getWorld().getName());
                pstmt.setInt(3, loc.getBlockX());
                pstmt.setInt(4, loc.getBlockY());
                pstmt.setInt(5, loc.getBlockZ());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // Remove a crop location asynchronously (e.g., when harvested or trampled)
    public void removeCrop(Location loc) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "DELETE FROM player_crops WHERE world = ? AND x = ? AND y = ? AND z = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, loc.getWorld().getName());
                pstmt.setInt(2, loc.getBlockX());
                pstmt.setInt(3, loc.getBlockY());
                pstmt.setInt(4, loc.getBlockZ());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // Check who owns a crop (Callback pattern handles the async result cleanly)
    public void getCropOwner(Location loc, java.util.function.Consumer<UUID> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "SELECT uuid FROM player_crops WHERE world = ? AND x = ? AND y = ? AND z = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, loc.getWorld().getName());
                pstmt.setInt(2, loc.getBlockX());
                pstmt.setInt(3, loc.getBlockY());
                pstmt.setInt(4, loc.getBlockZ());

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        UUID uuid = UUID.fromString(rs.getString("uuid"));
                        // Jump back to the main thread to safely interact with Bukkit/Players
                        Bukkit.getScheduler().runTask(plugin, () -> callback.accept(uuid));
                        return;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(null));
        });
    }
}
