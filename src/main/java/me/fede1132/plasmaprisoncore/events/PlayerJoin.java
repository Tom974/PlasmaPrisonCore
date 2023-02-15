package me.fede1132.plasmaprisoncore.events;

import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import me.fede1132.plasmaprisoncore.util.AsyncTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    private final PlasmaPrisonCore plugin = PlasmaPrisonCore.getInstance();
    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        plugin.database.onJoin(event.getPlayer().getName(), event.getPlayer().getUniqueId());;
        event.getPlayer().sendMessage("Putting stuff in array!");
        plugin.tokens.put(event.getPlayer().getUniqueId(), plugin.database.getTokens(event.getPlayer().getUniqueId(),true));
        event.getPlayer().sendMessage("array value: " + plugin.tokens.get(event.getPlayer().getUniqueId()));
    }
}
