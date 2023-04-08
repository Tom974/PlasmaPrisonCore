package me.mynqme.plasmaprisoncore.events;

import me.mynqme.plasmaprisoncore.PlasmaPrisonCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerJoin implements Listener {
    private final PlasmaPrisonCore plugin = PlasmaPrisonCore.getInstance();
    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        boolean firstJoined = plugin.database.onJoin(event.getPlayer().getName(), event.getPlayer().getUniqueId());
        if (firstJoined) {
            Bukkit.getLogger().warning("First join detected for " + event.getPlayer().getName());
           Player player = event.getPlayer();
           plugin.config.getStringList("first-join-commands").forEach(command -> {
               if (command.contains("%player%")) {
                   Bukkit.getLogger().info("Executing command: " + command + " for " + player.getName());
                   command = command.replaceAll("%player%", player.getName());
               }
               plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
           });
        }

        plugin.tokens.put(event.getPlayer().getUniqueId(), plugin.database.getTokens(event.getPlayer().getUniqueId(),true));
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
