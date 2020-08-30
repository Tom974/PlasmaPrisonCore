package me.fede1132.plasmaprisoncore.addons.basics.listeners;

import me.clip.autosell.AutoSellAPI;
import me.clip.autosell.objects.Multiplier;
import me.fede1132.plasmaprisoncore.enchant.BreakResult;
import me.fede1132.plasmaprisoncore.enchant.EnchantManager;
import me.fede1132.plasmaprisoncore.internal.events.FixedBreakBlockEvent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class AutoSeller implements Listener {
    @EventHandler
    public void onFixedBreak(FixedBreakBlockEvent event) {
        int lvl = EnchantManager.getInst().getEnchantLevel(event.getPlayer().getInventory().getItemInMainHand(), "fortune");
        int give = 1;
        if (lvl>0) give = new Random().nextInt(lvl);
        double money=0;
        for (BreakResult result : event.getResults()) {
            if (result==null) continue;
            for (Material old : result.getOldBlocks()) {
                if (old==Material.AIR) continue;
                if (AutoSellAPI.hasShop(event.getPlayer())) {
                    money+=AutoSellAPI.getCurrentShop(event.getPlayer()).getBaseWorth(new ItemStack(old, give));
                }
            }
        }
        Multiplier multiplier = AutoSellAPI.getMultiplier(event.getPlayer());
        money*=multiplier==null?1:multiplier.getMultiplier()+1;
        Bukkit.getServicesManager().getRegistration(Economy.class).getProvider().depositPlayer(event.getPlayer(), money);
    }
}
