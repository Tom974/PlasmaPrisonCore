package me.mynqme.plasmaprisoncore.events;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import de.tr7zw.nbtapi.NBTCompound;
import me.mynqme.plasmaprisoncore.PlasmaPrisonCore;
import me.mynqme.plasmaprisoncore.addons.AddonManager;
import me.mynqme.plasmaprisoncore.addons.basics.AddonBasics;
import me.mynqme.plasmaprisoncore.enchant.BreakResult;
import me.mynqme.plasmaprisoncore.enchant.Enchant;
import me.mynqme.plasmaprisoncore.enchant.EnchantManager;
import me.mynqme.plasmaprisoncore.internal.events.FixedBreakBlockEvent;
import org.bukkit.Bukkit;
//import me.clip.autosell.*;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class BlockBreak implements Listener {
    private final PlasmaPrisonCore core = PlasmaPrisonCore.getInstance();
    private final WorldGuardPlugin wgPlugin = WorldGuardPlugin.inst();
    private final RegionContainer container = wgPlugin.getRegionContainer();
    private RegionManager wgmanager;

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        // get region player is standing in
        Player player = event.getPlayer();
        wgmanager = container.get(event.getBlock().getWorld());
        assert wgmanager != null;
        if(wgmanager.getApplicableRegions(event.getBlock().getLocation()).getRegions().stream().noneMatch(region-> (region.getFlag(DefaultFlag.BLOCK_BREAK) == StateFlag.State.ALLOW && !region.getId().equalsIgnoreCase("mine-event")))) {
            return;
        }

        if (
            event.isCancelled() ||
           player.getInventory().getItemInMainHand() == null ||
            !player.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_PICKAXE) ||
            core.config.getStringList("blacklisted-worlds").contains(player.getWorld().getName())
        ) return;
        event.setDropItems(false);

        BlockBreakEvent finalEvent = event;
        NBTCompound enchantCompound = core.enchantManager.getEnchantCompound(player.getInventory().getItemInMainHand());
        Bukkit.getScheduler().runTaskAsynchronously(core, () -> { // NOTE TO SELF: Because of the async, the material gets changed to AIR
            List<BreakResult> results = core.enchantManager.registeredEnchants.values().stream()
                .filter(enchant -> enchantCompound.hasTag(enchant.getId()))
                .map(enchant -> enchant.onBreak(finalEvent))
                .collect(Collectors.toList());

            FixedBreakBlockEvent breakBlockEvent = new FixedBreakBlockEvent(player, results, enchantCompound);
            Bukkit.getPluginManager().callEvent(breakBlockEvent);
        });
    }
}
