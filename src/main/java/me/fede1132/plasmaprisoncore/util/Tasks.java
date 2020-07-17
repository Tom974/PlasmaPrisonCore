package me.fede1132.plasmaprisoncore.util;

import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import me.fede1132.plasmaprisoncore.actionbar.ActionBar;
import me.fede1132.plasmaprisoncore.internal.events.VaultEconomyEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Tasks {
    private final PlasmaPrisonCore instance;
    // Internal Start
    private final HashMap<Player, Double> vaultEconomy = new HashMap<>();
    // Internal End
    public Tasks(PlasmaPrisonCore instance) {
        this.instance = instance;
        taskVaultEconomy();
        taskActionBar();
    }

    /**
     * This task check every second of a change in the amount of player's money
     */
    private void taskVaultEconomy() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance,()->{
            Bukkit.getOnlinePlayers().forEach(player->{
                double eco = instance.econ.getBalance(player);
                if (!vaultEconomy.containsKey(player)) {
                    vaultEconomy.put(player, eco);
                    return;
                }
                double old = vaultEconomy.get(player);
                if (eco!=old) {
                    VaultEconomyEvent event = new VaultEconomyEvent(true, player, old, eco);
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        instance.econ.withdrawPlayer(player, eco);
                        instance.econ.depositPlayer(player, old);
                    }
                }
            });
        },1,20);
    }

    /**
     * This task checks every 2.5 seconds for new action bar messages
     */
    private void taskActionBar() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance,()->Bukkit.getOnlinePlayers().forEach(ActionBar::check),1,50);
    }
}
