package me.fede1132.plasmaprisoncore.util;

import me.fede1132.plasmaprisoncore.enchant.Enchant;
import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import me.fede1132.plasmaprisoncore.enchant.EnchantManager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Tasks {
    private final PlasmaPrisonCore instance;
    // Internal Start
    private final HashMap<Player, Double> vaultEconomy = new HashMap<>();

    private final EnchantManager manager = EnchantManager.getInst();

    // Internal End
    public Tasks(PlasmaPrisonCore instance) {
        this.instance = instance;
        //taskVaultEconomy();
        //taskActionBar();
        updateLore();
    }

    private void updateLore() {
        // Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
        //     // update the items lore
            
        //     Bukkit.getOnlinePlayers().forEach(player -> {
        //         player.sendMessage("Hey! I'm updating your lore!");
        //         if (player.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_PICKAXE)) {
        //             player.sendMessage("You were holding a pickaxe! Updating it now... Material type:" + player.getInventory().getItemInMainHand().getType().toString());
        //             // Enchant enchant = (Enchant)(EnchantManager.getInst()).registeredEnchants.get("efficiency");
        //             ItemStack item = this.manager.updatePickaxeLore(player.getInventory().getItemInMainHand(), player);
        //             player.sendMessage("Item type: " + item.getType().toString());
        //             player.getInventory().setItemInMainHand(item);
                    
        //             player.sendMessage("All done!");
        //         } else {
        //             player.sendMessage("You werent holding a pickaxe xd");
        //         }

        //     });
        // }, 20, 50); // after 20 seconds, start updating the lore every 5 seconds
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
//    }

    /**
     * This task checks every 2.5 seconds for new action bar messages
     */
//    private void taskActionBar() {
//        Bukkit.getScheduler().runTaskTimerAsynchronously(instance,()->Bukkit.getOnlinePlayers().forEach(ActionBar::check),1,50);
//    }
}
