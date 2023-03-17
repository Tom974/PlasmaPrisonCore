package me.mynqme.plasmaprisoncore.addons.basics;

import de.leonhard.storage.Yaml;
import de.leonhard.storage.sections.FlatFileSection;
import me.mynqme.plasmaprisoncore.PlasmaPrisonCore;
import me.mynqme.plasmaprisoncore.addons.Addon;
import me.mynqme.plasmaprisoncore.addons.basics.enchant.*;
import me.mynqme.plasmaprisoncore.addons.basics.enchant.*;
import me.mynqme.plasmaprisoncore.addons.basics.events.TokenChangeEvent;
import me.mynqme.plasmaprisoncore.addons.basics.listeners.AutoSeller;
import me.mynqme.plasmaprisoncore.addons.basics.listeners.ItemHeld;
import me.mynqme.plasmaprisoncore.internal.util.SimpleEntry;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import me.mynqme.multipliers.Api;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AddonBasics extends Addon {
    private static AddonBasics instance;
    public Yaml config;
    public long MAX_TOKENS;
    @Override
    public void load() {
        instance = this;
        registerEnchants(new EnchantEfficiency(), new EnchantFortune(), new EnchantHaste(), new EnchantJump(), new EnchantSpeed(), new EnchantMerchant());
        registerListeners(new AutoSeller(), new ItemHeld());
        registerCommands(new CmdPlasmaPrison());
        config = setupPersonalConfig("config", new SimpleEntry<>("max-tokens", "9223372036854775807"),
                new SimpleEntry<>("regions-blacklist", Arrays.asList("main")),
                new SimpleEntry<>("worlds-blacklist", Arrays.asList("main"))
        );

        try {
            MAX_TOKENS = Long.valueOf(config.getString("max-tokens"));
        } catch (NumberFormatException e) {
            MAX_TOKENS = Long.valueOf("9223372036854775807");
            PlasmaPrisonCore.getInstance().getLogger().warning("[PlasmaPrison - AddonBaiscs] There was an error trying to laod max-tokens value.. Using default max-tokens value.");
        }

        plugin.database.createTokensTable();
        plugin.database.createUsersTable();
    }

    /** Get player's tokens
     *
     * @param uuid Player's UUID
     * @return Player's tokens
     */
    public long getTokens(UUID uuid, boolean fromDatabase) {
        return plugin.database.getTokens(uuid, fromDatabase);
    }

    public HashMap<UUID, HashMap<String, Integer>> getCellValues() {
        return plugin.database.getCellValues();
    }

    public void removeCellValue(UUID uuid, String type, int amount) {
        plugin.database.removeCellValue(uuid, type, amount);
    }
    

    /** Set player's tokens
     *
     * @param uuid Player UUID
     * @param i Tokens to set
     */
    public void setTokensWithoutFiringEvent(UUID uuid, long i) {
        if (i > MAX_TOKENS) {
            i = MAX_TOKENS;
        } else if (i < 0) {
            i = 0;
        }

        Bukkit.getLogger().info("Setting tokens to " + i + " for " + uuid.toString() + "...");
        plugin.database.setTokens(uuid, i);
    }


    public void setTokens(UUID uuid, long i) {
        TokenChangeEvent event = new TokenChangeEvent(Bukkit.getPlayer(uuid), TokenChangeEvent.ChangeAction.SET, i);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        setTokensWithoutFiringEvent(uuid, event.getDifference());
    }

    public static void sellSeperateItem(Material material, long amount, Player player) {
        List<String> blocks = PlasmaPrisonCore.getInstance().config.getStringList("block-prices").stream().filter(s -> s.toLowerCase().startsWith(material.name().toLowerCase())).collect(Collectors.toList());
        if (blocks.size() == 0) { return; }
        String block = blocks.get(0);

        String[] split = block.split(":");
        player.sendMessage("Amount to sell: " + amount);
        if (!split[0].equalsIgnoreCase(material.name())) return;
        int price = Integer.parseInt(split[1]);
        long total = price * amount;
        // TODO: apply multiplier


        EconomyResponse r = PlasmaPrisonCore.getInstance().econ.depositPlayer(player, (double) total);
        if(!r.transactionSuccess()) {
            PlasmaPrisonCore.getInstance().getLogger().warning(String.format("An error occured: %s", r.errorMessage));
        }

    }

    public static void sellItems(List<ItemStack> toSell, Player player) {
        for (ItemStack item : toSell) {
//            player.sendMessage("itemstack data type: " + item.getType().name().toUpperCase() +":"+item.getData().getData());
            double price = PlasmaPrisonCore.getInstance().config.getDouble("block-prices." + item.getType().name().toUpperCase() + ":" + item.getData().getData() + ".price");
//            player.sendMessage("Price of this block is: " + price);
            if (price == 0.0) continue;
            int amount = item.getAmount();
            double total = (price * amount) * Api.getMultiplier(player, "sell");
            EconomyResponse r = PlasmaPrisonCore.getInstance().econ.depositPlayer(player, total);
//            player.sendMessage("Selling " + amount + " " + item.getType().name() + " for " + total + " $");
            if(!r.transactionSuccess()) {
                PlasmaPrisonCore.getInstance().getLogger().warning(String.format("An error occured: %s", r.errorMessage));
            }
        }
    }

    /** Add player's tokens
     *
     * @param uuid Player's UUID
     * @param i Token to add
     */
    public void addTokensWithoutFiringEvent(UUID uuid, long i) {
        plugin.database.addTokens(uuid, i, this.MAX_TOKENS);
    }

    public void addTokens(UUID uuid, long i) {
        TokenChangeEvent event = new TokenChangeEvent(Bukkit.getPlayer(uuid), TokenChangeEvent.ChangeAction.ADD, i);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        addTokensWithoutFiringEvent(uuid, event.getDifference());

        // call event TokenChangeEvent
        
    }

    /** Remove player tokens
     *
     * @param uuid Player's UUID
     * @param i Tokens to remove
     */
    public void removeTokensWithoutFiringEvent(UUID uuid, long i) {
        plugin.database.removeTokens(uuid, i);
    }

    public void removeTokens(UUID uuid, long i) {
        TokenChangeEvent event = new TokenChangeEvent(Bukkit.getPlayer(uuid), TokenChangeEvent.ChangeAction.REMOVE, i);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        removeTokensWithoutFiringEvent(uuid, event.getDifference());
    }

    public static AddonBasics getInstance() {
        return instance;
    }
    
    public static AddonBasics getInst() {
        return instance;
    }
}
