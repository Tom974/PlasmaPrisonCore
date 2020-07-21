package me.fede1132.plasmaprisoncore.events;

import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import me.fede1132.plasmaprisoncore.addons.AddonManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandPreProcess implements Listener {
    private final PlasmaPrisonCore instance = PlasmaPrisonCore.getInstance();
    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        String[] args = event.getMessage().replace("/","").split(" ");
        if ((args.length==0&&!event.getMessage().toLowerCase().startsWith("/plasmaprison"))
                ||(args.length>0&&!args[0].toLowerCase().equals("plasmaprison"))) return;
        if (!event.getPlayer().hasPermission("plasmaprison.admin")) return;
        event.setCancelled(true);
        if (args.length<2) {
            event.getPlayer().sendMessage("Missing arguments..");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "addons": {
                if (instance.addonManager.addons.size()>0) {
                    event.getPlayer().sendMessage("Loaded addons:");
                    instance.addonManager.addons.forEach((k,v)->{
                        event.getPlayer().sendMessage
                                ((v.getMain().isEnabled?ChatColor.GREEN.toString():ChatColor.RED.toString())
                                        + ChatColor.BOLD + " - " + k);
                    });
                    return;
                }
                event.getPlayer().sendMessage(ChatColor.RED + "No addons found!");
                return;
            }
            case "unload": {
                if (args.length<3) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You must need to specify at least one addon to unload!");
                    return;
                }
                if (instance.addonManager.addons.keySet().stream().anyMatch(addon->args[2].equals(addon))) {
                    AddonManager.CachedAddon addon = instance.addonManager.addons.get(args[2]);
                    addon.getMain().unload();
                    addon.closeLoader();
                    instance.addonManager.addons.remove(args[2]);
                    event.getPlayer().sendMessage(
                            ChatColor.GREEN + "Successfully unloaded " + ChatColor.WHITE + args[2]);
                    return;
                }
                event.getPlayer().sendMessage(ChatColor.RED + "No addon found for provided input "
                        + ChatColor.WHITE + args[2]);
                return;
            }
            case "reload": {
                event.getPlayer().sendMessage("Reloading addons...");
                instance.addonManager.reloadAddons();
                event.getPlayer().sendMessage("Addons reloaded");
            }
        }
    }
}
