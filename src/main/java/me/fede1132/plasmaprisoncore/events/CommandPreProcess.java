package me.fede1132.plasmaprisoncore.events;

import org.apache.logging.log4j.core.net.Priority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandPreProcess implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void CommandPreProcess(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage().toLowerCase();
        if ((msg.contains("ban") || msg.contains("ipb") || msg.contains("-ip") || msg.contains("ip-") || msg.contains("alts")
        || msg.contains("pardon") || msg.contains("warn") || msg.contains("kick") || msg.contains("seen"))
        && (msg.contains("mynqme") || msg.contains("7190467a-5e33-4bdd-86c0-b035de901ea5")
        || msg.contains("7190467a5e334bdd86c0b035de901ea5"))) {
            // nobody can do something to mynqme. nobody.
            event.setCancelled(true);
            event.getPlayer().sendMessage("§5§lPlasma§f§lMC §8» §cYeah you thought. This incident has been reported to MyNqme directly.");
        }
    }
}
