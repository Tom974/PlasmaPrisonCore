package me.fede1132.plasmaprisoncore.events;

import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import me.fede1132.plasmaprisoncore.enchant.BreakResult;
import me.fede1132.plasmaprisoncore.enchant.EnchantManager;
import me.fede1132.plasmaprisoncore.internal.events.FixedBreakBlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.stream.Collectors;

public class BlockBreak implements Listener {
    private final PlasmaPrisonCore core = PlasmaPrisonCore.getInstance();
    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        event.setDropItems(false);
        if (event.isCancelled() || event.getPlayer().getInventory().getItemInMainHand()==null || event.getPlayer().getInventory().getItemInMainHand().getType()== Material.AIR) return;
        Bukkit.getScheduler().runTaskAsynchronously(core, ()->{
            List<BreakResult> results = core.enchantManager.registeredEnchants.values().stream().filter(enchant -> EnchantManager.getInst().hasEnchant(event.getPlayer().getInventory().getItemInMainHand(), enchant.getId())).map(enchant->enchant.onBreak(event)).collect(Collectors.toList());
            FixedBreakBlockEvent breakBlockEvent = new FixedBreakBlockEvent(event.getPlayer(), results);
            Bukkit.getPluginManager().callEvent(breakBlockEvent);
        });
    }
}
