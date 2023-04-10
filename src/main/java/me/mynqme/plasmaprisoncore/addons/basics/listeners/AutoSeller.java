package me.mynqme.plasmaprisoncore.addons.basics.listeners;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
//import me.clip.autosell.AutoSellAPI;
//import me.clip.autosell.SellHandler;
import me.mynqme.plasmaprisoncore.PlasmaPrisonCore;
import me.mynqme.plasmaprisoncore.addons.basics.AddonBasics;
import me.mynqme.plasmaprisoncore.enchant.BreakResult;
import me.mynqme.plasmaprisoncore.enchant.Enchant;
import me.mynqme.plasmaprisoncore.enchant.EnchantManager;
import me.mynqme.plasmaprisoncore.internal.events.FixedBreakBlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class AutoSeller implements Listener {
    private final PlasmaPrisonCore core = PlasmaPrisonCore.getInstance();
    private final WorldGuardPlugin wgPlugin = WorldGuardPlugin.inst();
    private final RegionContainer container = wgPlugin.getRegionContainer();
    private RegionManager wgmanager;

    @EventHandler
    public void onNormalBreak(BlockBreakEvent event) {
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
        ) {
            return;
        }
        event.setDropItems(false);

        int lvl = EnchantManager.getInst().getEnchantLevel(player.getInventory().getItemInMainHand(), "fortune");
        List<ItemStack> toSell = new ArrayList<>();
        MaterialData data = event.getBlock().getState().getData();
        ItemStack itemToSell = new ItemStack(event.getBlock().getType(), (lvl > 0 ? new Random().nextInt(lvl) : 0));
        itemToSell.setData(data);
        toSell.add(itemToSell);
        int lvlMerchant = EnchantManager.getInst().getEnchantLevel(player.getInventory().getItemInMainHand(), "merchant");
        boolean merchantProc;
        if (lvlMerchant > 0) {
            Enchant enchant = EnchantManager.getInst().registeredEnchants.get("merchant");
            merchantProc = chance(enchant.max, lvlMerchant, enchant.maxChance);
        } else {
            merchantProc = false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(core, () -> {
            AddonBasics.sellItems(toSell, player, merchantProc);
        });
    }
    @EventHandler
    public void onFixedBreak(FixedBreakBlockEvent event) {
        // handle explosion drops
//        if (!AutoSellAPI.hasShop(event.getPlayer())) return;
        Player player = event.getPlayer();
        if (!player.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_PICKAXE)) return;
        int lvlFortune = EnchantManager.getInst().getEnchantLevelFromCompound(event.getNBTCompound(), "fortune");
        List<ItemStack> toSell = event.getResults()
            .stream() // being of the stream
            .filter(Objects::nonNull) // null check
            .map(BreakResult::getOldBlocks) // map to stream of list of materials
            .filter(Objects::nonNull) // null check
            .flatMap(List::stream) // map to stream of materials
            .filter(Objects::nonNull) // null check
            .filter(material -> material != Material.AIR && material != Material.BEDROCK && material.isBlock()) // filter for material that is not AIR or it isn't a block
            .map(material->new ItemStack(material, lvlFortune > 0 ? new Random().nextInt(lvlFortune) : 1)) // map to stream ot itemstack
            .collect(Collectors.toList()); // collect everything to a simple list

        if (toSell.isEmpty()) return;

        int lvlMerchant = EnchantManager.getInst().getEnchantLevelFromCompound(event.getNBTCompound(), "merchant");
        boolean merchantProc;
        if (lvlMerchant > 0) {
            Enchant enchant = EnchantManager.getInst().registeredEnchants.get("merchant");
            merchantProc = chance(enchant.max, lvlMerchant, enchant.maxChance);
        } else {
            merchantProc = false;
        }

        // get all itemstacks that are the same material and add them together
        Bukkit.getScheduler().runTaskAsynchronously(core, () -> {
            List<ItemStack> finalToSell = toSell.stream().collect(Collectors.groupingBy(ItemStack::getType)).values().stream().map(itemStacks -> {
                ItemStack itemStack = itemStacks.get(0);
                itemStack.setAmount(itemStacks.stream().mapToInt(ItemStack::getAmount).sum());
                return itemStack;
            }).collect(Collectors.toList());

//            Bukkit.getLogger().info("toSell itemstack size: " + finalToSell.size());
            AddonBasics.sellItems(finalToSell, player, merchantProc);
        });
    }

    private boolean chance(int max, int lvl, double maxChance) {
        double percent = ((double) lvl / (double) max) * maxChance;
        if (percent>maxChance) percent = maxChance;
        double random = ThreadLocalRandom.current().nextDouble(0.0, 100D);
        return percent >= random;
    }
}
