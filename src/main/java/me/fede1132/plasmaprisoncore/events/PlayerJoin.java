package me.fede1132.plasmaprisoncore.events;

import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerJoin implements Listener {
    private final PlasmaPrisonCore instance = PlasmaPrisonCore.getInstance();
    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        try (Connection connection = instance.database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE uuid = ?");
            ps.setString(1,event.getPlayer().getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            boolean b = rs.next();
            if (!b||!rs.getString("name").equals(event.getPlayer().getName())) {
                PreparedStatement insert = connection.prepareStatement(
                        b?"UPDATE users SET name = ? WHERE uuid = ?":
                        "INSERT INTO users (name, uuid) VALUES (?, ?)");
                insert.setString(1, event.getPlayer().getName());
                insert.setString(2, event.getPlayer().getUniqueId().toString());
                insert.executeUpdate();
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
