package me.mynqme.plasmaprisoncore.events;

import me.mynqme.plasmaprisoncore.PlasmaPrisonCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeave implements Listener {
    private final PlasmaPrisonCore plugin = PlasmaPrisonCore.getInstance();
    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerQuitEvent event) {
        plugin.database.onLeave(event.getPlayer().getUniqueId());
        plugin.tokens.remove(event.getPlayer().getUniqueId()); // remove from hashmap as it is not needed anymore. aka fix memory
    }
}
