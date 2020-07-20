package me.fede1132.plasmaprisoncore.events;

import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandPreProcess implements Listener {
    private final PlasmaPrisonCore instance = PlasmaPrisonCore.getInstance();
    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        String[] args = event.getMessage().replace("/","").toLowerCase().split(" ");
        if ((args.length==0&&!event.getMessage().toLowerCase().startsWith("/plasmaprison"))
                ||(args.length>0&&args[0].equals("plasmaprison"))) return;
        if (!event.getPlayer().hasPermission("plasmaprison.admin")) return;
        event.setCancelled(true);
        if (args.length<2) {
            event.getPlayer().sendMessage("Missing arguments..");
            return;
        }
        if (args[1].equalsIgnoreCase("addons")) {
            if (instance.addonManager.addons.size()>0) {
                event.getPlayer().sendMessage("Loaded addons:");
                for (String addon : instance.addonManager.addons.keySet().toArray(new String[0])) {
                    event.getPlayer().sendMessage(ChatColor.GREEN + " - " + addon);
                }
                return;
            }
            event.getPlayer().sendMessage(ChatColor.RED + "No addons found!");
            return;
        }
    }
}
