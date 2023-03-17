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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
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
        event.setDropItems(false);
        // get region player is standing in
        wgmanager = container.get(event.getBlock().getWorld());
        assert wgmanager != null;
        if(!wgmanager.getApplicableRegions(event.getBlock().getLocation()).getRegions().stream().anyMatch(region-> (region.getFlag(DefaultFlag.BLOCK_BREAK) == StateFlag.State.ALLOW && !region.getId().equalsIgnoreCase("mine-event")))) {
            return;
        }

        if (
            event.isCancelled() ||
            event.getPlayer().getInventory().getItemInMainHand() == null ||
            event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR
        ) return;
        Bukkit.getScheduler().runTaskAsynchronously(core, () -> { // NOTE TO SELF: Because of the async, the material gets changed to AIR
            if (!event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_PICKAXE)) return;
            NBTCompound enchants = core.enchantManager.getEnchantCompound(event.getPlayer().getInventory().getItemInMainHand());
            List<BreakResult> results = core.enchantManager.registeredEnchants.values().stream()
                .filter(enchant -> enchants.hasTag(enchant.getId()))
                .map(enchant -> enchant.onBreak(event))
                .collect(Collectors.toList());
            FixedBreakBlockEvent breakBlockEvent = new FixedBreakBlockEvent(event.getPlayer(), results);
            Bukkit.getPluginManager().callEvent(breakBlockEvent);
        });

        // Handle individual drops
//        if (!AutoSellAPI.hasShop(event.getPlayer())) return;
//        int lvlFortune = EnchantManager.getInst().getEnchantLevel(event.getPlayer().getInventory().getItemInMainHand(), "fortune");
//
//        List<ItemStack> drops = new ArrayList<>();
//        drops.add(new ItemStack(event.getBlock().getType(), 1));
//        if (event.getBlock().getType() == Material.AIR || !event.getBlock().getType().isBlock()) return;
//
//        List<ItemStack> toSell = drops
//                .stream() // being of the stream
//                .filter(material -> material.getType() != Material.AIR && material.getType().isBlock()) // filter for material that is not AIR or it isn't a block
//                .map(material->new ItemStack(material.getType(), lvlFortune > 0 ? new Random().nextInt(lvlFortune) : 1)) // map to stream ot itemstack
//                .collect(Collectors.toList()); // collect everything to a simple list

//        long amount = (lvlFortune > 0 ? new Random().nextInt(lvlFortune) : 1);
//        int lvlMerchant =EnchantManager.getInst().getEnchantLevel(event.getPlayer().getInventory().getItemInMainHand(), "merchant");
//        if (lvlMerchant > 0) {
//            Enchant enchant = EnchantManager.getInst().registeredEnchants.get("merchant");
//            if (chance(enchant.max, lvlMerchant, enchant.maxChance)) {
//                // merchant proc! double the items
//                amount *= 2;
//            }
//        }
//
//        AddonBasics.sellSeperateItem(event.getBlock().getType(), amount, event.getPlayer());
//        AddonBasics.sellItems(toSell, event.getPlayer());
//        SellHandler.sellItems(event.getPlayer(), toSell, AutoSellAPI.getCurrentShop(event.getPlayer()));
    }
}
