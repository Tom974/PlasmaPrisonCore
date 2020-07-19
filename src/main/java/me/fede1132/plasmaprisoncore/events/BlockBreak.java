package me.fede1132.plasmaprisoncore.events;

import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class BlockBreak implements Listener {
    private static final PlasmaPrisonCore instance = PlasmaPrisonCore.getInstance();
    // AUTO PICKUP
    @EventHandler(priority = EventPriority.LOW)
    public void onBreak(BlockBreakEvent event) {
        if (event.isCancelled() && !instance.config.getOrSetDefault("auto-pickup.enabled", true)) return;
        event.setDropItems(false);
        List<String> blacklist = instance.config.getStringList("auto-pickup.blacklist");
        if (blacklist.size()>0) {
            for (String block : blacklist) {
                if (block.contains("*")) {
                    if (block.startsWith("*") && event.getBlock().getType().toString().toLowerCase().endsWith(block.toLowerCase())) return;
                    else if (block.endsWith("*") && event.getBlock().getType().toString().toLowerCase().startsWith(block.toLowerCase())) return;
                } else {
                    if (event.getBlock().getType().toString().toLowerCase().equals(block.toLowerCase())) return;
                }
            }
        }
        Material toGive = event.getBlock().getType();
        List<String> replace = instance.config.getStringList("auto-pickup.replace");
        if (replace.size()>0) {
            for (String str : replace) {
                String[] split = str.split(":");
                if (split[0].contains("*")) {
                    if (split[0].startsWith("*") && event.getBlock().getType().toString().toLowerCase().endsWith(split[0].toLowerCase()))
                        toGive = Material.getMaterial(split[1]);
                    else if (split[0].endsWith("*") && event.getBlock().getType().toString().toLowerCase().startsWith(split[0].toLowerCase()))
                        toGive = Material.getMaterial(split[1]);
                } else {
                    if (event.getBlock().getType().toString().toLowerCase().equals(split[0].toLowerCase()))
                        toGive = Material.getMaterial(split[1]);
                }
            }
        }
        if (toGive==null) toGive = Material.STONE;
        int count = 1;
        if (event.getPlayer().getInventory().getItemInMainHand()!=null
                &&event.getPlayer().getInventory().getItemInMainHand().getType().toString().endsWith("PICKAXE")) {
            ItemStack pick = event.getPlayer().getInventory().getItemInMainHand();
            if (pick.containsEnchantment(Enchantment.LUCK)) {
                int rnd = new Random().nextInt(pick.getEnchantmentLevel(Enchantment.LUCK));
                count = rnd>0?rnd:1;
            }
        }
        event.getPlayer().getInventory().addItem(new ItemStack(toGive, count));
    }
}
