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
    private final PlasmaPrisonCore plugin = PlasmaPrisonCore.getInstance();
    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        new AsyncTask(() -> {
            plugin.database.onJoin(event.getPlayer().getName(), event.getPlayer().getUniqueId());;
        });
        event.getPlayer().sendMessage("Putting stuff in array!");
        plugin.tokens.put(event.getPlayer().getUniqueId(), plugin.database.getTokens(event.getPlayer().getUniqueId(), true));
        event.getPlayer().sendMessage("array value: " + plugin.tokens.get(event.getPlayer().getUniqueId()));
    }
}
