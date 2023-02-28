package me.mynqme.plasmaprisoncore.events;

import me.mynqme.plasmaprisoncore.PlasmaPrisonCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    private final PlasmaPrisonCore plugin = PlasmaPrisonCore.getInstance();
    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        plugin.database.onJoin(event.getPlayer().getName(), event.getPlayer().getUniqueId());
        plugin.tokens.put(event.getPlayer().getUniqueId(), plugin.database.getTokens(event.getPlayer().getUniqueId(),true));
    }
}
