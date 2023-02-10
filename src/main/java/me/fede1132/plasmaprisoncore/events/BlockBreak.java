package me.fede1132.plasmaprisoncore.events;

import de.tr7zw.nbtapi.NBTCompound;
import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import me.fede1132.plasmaprisoncore.enchant.BreakResult;
import me.fede1132.plasmaprisoncore.enchant.EnchantManager;
import me.fede1132.plasmaprisoncore.internal.events.FixedBreakBlockEvent;
import org.bukkit.Bukkit;
import me.clip.autosell.*;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class BlockBreak implements Listener {
    private final PlasmaPrisonCore core = PlasmaPrisonCore.getInstance();
    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        event.setDropItems(false);
        if (event.isCancelled() || event.getPlayer().getInventory().getItemInMainHand()==null || event.getPlayer().getInventory().getItemInMainHand().getType()== Material.AIR) return;
        Bukkit.getScheduler().runTaskAsynchronously(core, ()->{
            NBTCompound enchants = core.enchantManager.getEnchantCompound(event.getPlayer().getInventory().getItemInMainHand());
            List<BreakResult> results = core.enchantManager.registeredEnchants.values().stream()
                    .filter(enchant -> enchants.hasTag(enchant.getId()))
                    .map(enchant->enchant.onBreak(event)).collect(Collectors.toList());
            FixedBreakBlockEvent breakBlockEvent = new FixedBreakBlockEvent(event.getPlayer(), results);
            Bukkit.getPluginManager().callEvent(breakBlockEvent);
        });
        

        // Handle individual drops
        if (!AutoSellAPI.hasShop(event.getPlayer())) return;
        int lvl = EnchantManager.getInst().getEnchantLevel(event.getPlayer().getInventory().getItemInMainHand(), "fortune");
        List<ItemStack> toSell = event.getBlock().getDrops()
                .stream() // being of the stream
                .filter(material -> material.getType() != Material.AIR && material.getType().isBlock()) // filter for material that is not AIR or it isn't a block
                .map(material->new ItemStack(material.getType(), lvl>0?new Random().nextInt(lvl):1)) // map to stream ot itemstack
                .collect(Collectors.toList()); // collect everything to a simple list
        
        SellHandler.sellItems(event.getPlayer(), toSell, AutoSellAPI.getCurrentShop(event.getPlayer()));
    }
}
