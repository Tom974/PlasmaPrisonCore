package me.fede1132.plasmaprisoncore.addons.basics;

import me.fede1132.plasmaprisoncore.addons.Addon;
import me.fede1132.plasmaprisoncore.addons.basics.enchant.*;
import me.fede1132.plasmaprisoncore.addons.basics.listeners.AutoSeller;
import me.fede1132.plasmaprisoncore.addons.basics.listeners.ItemHeld;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AddonBasics extends Addon {
    private static AddonBasics instance;
    @Override
    public void load() {
        instance = this;
        registerEnchants(new EnchantEfficiency(), new EnchantFortune(), new EnchantHaste(), new EnchantJump(), new EnchantSpeed());
        registerListeners(new AutoSeller(), new ItemHeld());
        registerCommands(new CmdPlasmaPrison());
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
    public long getTokens(UUID uuid) {
        try (Connection connection = plugin.database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM tokens WHERE uuid = ?");
            ps.setString(1,uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("tokens");
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** Set player's tokens
     *
     * @param uuid Player UUID
     * @param i Tokens to set
     */
    public void setTokens(UUID uuid, long i) {
        try (Connection connection = plugin.database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM tokens WHERE uuid = ?");
            ps.setString(1,uuid.toString());
            ResultSet rs = ps.executeQuery();
            PreparedStatement query = connection.prepareStatement(rs.next()?
                    "UPDATE tokens SET tokens = ? WHERE uuid = ?":
                    "INSERT INTO tokens (tokens,uuid) VALUES (?, ?)");
            query.setString(1,String.valueOf(i));
            query.setString(2,uuid.toString());
            query.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Add player's tokens
     *
     * @param uuid Player's UUID
     * @param i Token to add
     */
    public void addTokens(UUID uuid, long i) {
        try (Connection connection = plugin.database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM tokens WHERE uuid = ?");
            ps.setString(1,uuid.toString());
            ResultSet rs = ps.executeQuery();
            PreparedStatement query = connection.prepareStatement(rs.next()?
                    "UPDATE tokens SET tokens = tokens + ? WHERE uuid = ?":
                    "INSERT INTO tokens (tokens,uuid) VALUES (?, ?)");
            query.setString(1,String.valueOf(i));
            query.setString(2,uuid.toString());
            query.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Remove player tokens
     *
     * @param uuid Player's UUID
     * @param i Tokens to remove
     */
    public void removeTokens(UUID uuid, long i) {
        try (Connection connection = plugin.database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM tokens WHERE uuid = ?");
            ps.setString(1,uuid.toString());
            ResultSet rs = ps.executeQuery();
            boolean b = rs.next();
            PreparedStatement query = connection.prepareStatement(b?
                    "UPDATE tokens SET tokens = tokens - ? WHERE uuid = ?":
                    "INSERT INTO tokens (tokens,uuid) VALUES (?, ?)");
            query.setString(1,b?String.valueOf(i):"0");
            query.setString(2,uuid.toString());
            query.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static AddonBasics getInstance() {
        return instance;
    }
}
