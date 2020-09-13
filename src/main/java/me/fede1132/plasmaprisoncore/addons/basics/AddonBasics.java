package me.fede1132.plasmaprisoncore.addons.basics;

import me.fede1132.f32lib.shaded.storage.Yaml;
import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import me.fede1132.plasmaprisoncore.addons.Addon;
import me.fede1132.plasmaprisoncore.addons.basics.enchant.*;
import me.fede1132.plasmaprisoncore.addons.basics.events.TokenChangeEvent;
import me.fede1132.plasmaprisoncore.addons.basics.listeners.AutoSeller;
import me.fede1132.plasmaprisoncore.addons.basics.listeners.ItemHeld;
import me.fede1132.plasmaprisoncore.internal.util.SimpleEntry;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AddonBasics extends Addon {
    private static AddonBasics instance;
    public Yaml config;
    public BigDecimal MAX_TOKENS;
    @Override
    public void load() {
        instance = this;
        registerEnchants(new EnchantEfficiency(), new EnchantFortune(), new EnchantHaste(), new EnchantJump(), new EnchantSpeed());
        registerListeners(new AutoSeller(), new ItemHeld());
        registerCommands(new CmdPlasmaPrison());
        config = setupPersonalConfig("config",
                new SimpleEntry<>("max-tokens", "10000000000000000000000000"));
        try {
            MAX_TOKENS = new BigDecimal(config.getString("max-tokens"));
        } catch (NumberFormatException e) {
            MAX_TOKENS = new BigDecimal("10000000000000000000000000");
            PlasmaPrisonCore.getInstance().getLogger().warning("[PlasmaPrison - AddonBaiscs] There was an error trying to laod max-tokens value.. Using default max-tokens value.");
        }
        try (Connection connection = plugin.database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS tokens (\n" +
                            "  `uuid` VARCHAR(36) NOT NULL,\n" +
                            "  `tokens` TEXT NOT NULL,\n" +
                            "  PRIMARY KEY (`uuid`));\n");
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /** Get player's tokens
     *
     * @param uuid Player's UUID
     * @return Player's tokens
     */
    public BigDecimal getTokens(UUID uuid) {
        try (Connection connection = plugin.database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM tokens WHERE uuid = ?");
            ps.setString(1,uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getBigDecimal("tokens");
            return BigDecimal.ZERO;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /** Set player's tokens
     *
     * @param uuid Player UUID
     * @param i Tokens to set
     */
    public void setTokensWithoutFiringEvent(UUID uuid, BigDecimal i) {
        try (Connection connection = plugin.database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM tokens WHERE uuid = ?");
            ps.setString(1,uuid.toString());
            ResultSet rs = ps.executeQuery();
            PreparedStatement query = connection.prepareStatement(rs.next()?
                    "UPDATE tokens SET tokens = ? WHERE uuid = ?":
                    "INSERT INTO tokens (tokens,uuid) VALUES (?, ?)");
            if (i.compareTo(MAX_TOKENS)>0) {
                i = MAX_TOKENS;
            } else if (i.compareTo(BigDecimal.ZERO)<0) {
                i = BigDecimal.ZERO;
            }
            query.setString(1,String.valueOf(i));
            query.setString(2,uuid.toString());
            query.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void setTokens(UUID uuid, BigDecimal i) {
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
    public void addTokensWithoutFiringEvent(UUID uuid, BigDecimal i) {
        try (Connection connection = plugin.database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM tokens WHERE uuid = ?");
            ps.setString(1,uuid.toString());
            ResultSet rs = ps.executeQuery();
            PreparedStatement query = connection.prepareStatement(rs.next()?
                    "UPDATE tokens SET tokens = ? WHERE uuid = ?":
                    "INSERT INTO tokens (tokens,uuid) VALUES (?, ?)");
            i = i.add(rs.getBigDecimal("tokens"));
            if (i.compareTo(MAX_TOKENS)>0) {
                i = MAX_TOKENS;
            }
            query.setString(1,i.toString());
            query.setString(2,uuid.toString());
            query.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addTokens(UUID uuid, BigDecimal i) {
        TokenChangeEvent event = new TokenChangeEvent(Bukkit.getPlayer(uuid), TokenChangeEvent.ChangeAction.ADD, i);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        addTokensWithoutFiringEvent(uuid, event.getDifference());
    }

    /** Remove player tokens
     *
     * @param uuid Player's UUID
     * @param i Tokens to remove
     */
    public void removeTokensWithoutFiringEvent(UUID uuid, BigDecimal i) {
        try (Connection connection = plugin.database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM tokens WHERE uuid = ?");
            ps.setString(1,uuid.toString());
            ResultSet rs = ps.executeQuery();
            boolean b = rs.next();
            PreparedStatement query = connection.prepareStatement(b?
                    "UPDATE tokens SET tokens = ? WHERE uuid = ?":
                    "INSERT INTO tokens (tokens,uuid) VALUES (?, ?)");
            i = rs.getBigDecimal("tokens").subtract(i);
            if (i.compareTo(BigDecimal.ZERO)<0) {
                i = BigDecimal.ZERO;
            }
            query.setString(1,b?i.toString():"0");
            query.setString(2,uuid.toString());
            query.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeTokens(UUID uuid, BigDecimal i) {
        TokenChangeEvent event = new TokenChangeEvent(Bukkit.getPlayer(uuid), TokenChangeEvent.ChangeAction.REMOVE, i);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        removeTokensWithoutFiringEvent(uuid, event.getDifference());
    }

    public static AddonBasics getInstance() {
        return instance;
    }
}
