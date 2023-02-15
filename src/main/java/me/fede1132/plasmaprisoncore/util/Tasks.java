package me.fede1132.plasmaprisoncore.util;

import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import org.bukkit.Bukkit;
import java.util.Map;
import java.util.UUID;

public class Tasks {
    private final PlasmaPrisonCore instance;

    public Tasks(PlasmaPrisonCore instance) {
        this.instance = instance;
        //taskVaultEconomy();
        taskUpdateTokens();
    }

    private void taskUpdateTokens() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            for (Map.Entry<UUID, Long> map : this.instance.tokens.entrySet()) {
                this.instance.database.saveTokens(map.getKey(), map.getValue());
            }
        }, 20 * 60 * 5 /*5min */, 20 * 60 * 10 /*10min */);
    }


    /**
     * This task check every second of a change in the amount of player's money
     */
//    private void taskVaultEconomy() {
//        Bukkit.getScheduler().runTaskTimerAsynchronously(instance,()-> Bukkit.getOnlinePlayers().forEach(player->{
//            double eco = instance.econ.getBalance(player);
//            if (!vaultEconomy.containsKey(player)) {
//                vaultEconomy.put(player, eco);
//                return;
//            }
//            double old = vaultEconomy.get(player);
//            if (eco!=old) {
//                VaultEconomyEvent event = new VaultEconomyEvent(true, player, old, eco);
//                Bukkit.getPluginManager().callEvent(event);
//                if (event.isCancelled()) {
//                    instance.econ.withdrawPlayer(player, eco);
//                    instance.econ.depositPlayer(player, old);
//                    return;
//                }
//                vaultEconomy.put(player,eco);
//                if (eco>old) {
//                    ActionBar.ActionBarTypes.ECONOMY.fire(event.getPlayer(), new Placeholder("money",
//                            String.valueOf(eco-old)));
//                }
//            }
//        }),1,20);
//     }
}
