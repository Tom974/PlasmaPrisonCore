package me.fede1132.plasmaprisoncore.addons.basics;

import de.leonhard.storage.Yaml;
import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import me.fede1132.plasmaprisoncore.addons.Addon;
import me.fede1132.plasmaprisoncore.addons.basics.enchant.*;
import me.fede1132.plasmaprisoncore.addons.basics.events.TokenChangeEvent;
import me.fede1132.plasmaprisoncore.addons.basics.listeners.AutoSeller;
import me.fede1132.plasmaprisoncore.addons.basics.listeners.ItemHeld;
import me.fede1132.plasmaprisoncore.internal.util.SimpleEntry;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

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
        config = setupPersonalConfig("config", new SimpleEntry<>("max-tokens", "9223372036854775807"));
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

//        Bukkit.getLogger().info("Setting tokens to " + i + " for " + uuid.toString() + "...");

        plugin.database.setTokens(uuid, i);
    }


    public void setTokens(UUID uuid, long i) {
        TokenChangeEvent event = new TokenChangeEvent(Bukkit.getPlayer(uuid), TokenChangeEvent.ChangeAction.SET, i);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        setTokensWithoutFiringEvent(uuid, event.getDifference());
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
