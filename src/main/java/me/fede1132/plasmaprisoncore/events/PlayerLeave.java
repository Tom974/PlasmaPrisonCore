package me.fede1132.plasmaprisoncore.events;

import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerLeave implements Listener {
    private final PlasmaPrisonCore plugin = PlasmaPrisonCore.getInstance();
    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerQuitEvent event) {
        plugin.database.onLeave(event.getPlayer().getName(), event.getPlayer().getUniqueId());
    }
}
