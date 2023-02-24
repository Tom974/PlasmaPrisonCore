package me.mynqme.plasmaprisoncore.addons.basics.listeners;

import me.clip.autosell.AutoSellAPI;
import me.clip.autosell.SellHandler;
import me.mynqme.plasmaprisoncore.enchant.BreakResult;
import me.mynqme.plasmaprisoncore.enchant.EnchantManager;
import me.mynqme.plasmaprisoncore.internal.events.FixedBreakBlockEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class AutoSeller implements Listener {
    @EventHandler
    public void onFixedBreak(FixedBreakBlockEvent event) {
        // handle explosion drops
        if (!AutoSellAPI.hasShop(event.getPlayer())) return;
        int lvl = EnchantManager.getInst().getEnchantLevel(event.getPlayer().getInventory().getItemInMainHand(), "fortune");
        List<ItemStack> toSell = event.getResults()
                .stream() // being of the stream
                .filter(Objects::nonNull) // null check
                .map(BreakResult::getOldBlocks) // map to stream of list of materials
                .filter(Objects::nonNull) // null check
                .flatMap(List::stream) // map to stream of materials
                .filter(Objects::nonNull) // null check
                .filter(material -> material != Material.AIR && material.isBlock()) // filter for material that is not AIR or it isn't a block
                .map(material->new ItemStack(material, lvl>0?new Random().nextInt(lvl):1)) // map to stream ot itemstack
                .collect(Collectors.toList()); // collect everything to a simple list
        SellHandler.sellItems(event.getPlayer(), toSell, AutoSellAPI.getCurrentShop(event.getPlayer()));
    }
}
