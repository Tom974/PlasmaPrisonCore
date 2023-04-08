package me.mynqme.plasmaprisoncore.internal.util.sql;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.mynqme.plasmaprisoncore.PlasmaPrisonCore;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;

public class Database {
    private static Database inst;
    private final ConnectionPoolManager pool;
    private final PlasmaPrisonCore plugin;
    public Database(PlasmaPrisonCore instance) {
        this.plugin = instance;
        inst = this;
        pool = new ConnectionPoolManager(plugin);
    }

    public boolean getExplosiveStatus(UUID uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean status = true;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM `explosion_prefs` WHERE `uuid` = ?");
            ps.setString(1, uuid.toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                status = Boolean.parseBoolean(rs.getString("explosive"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
            return status;
        }
    }

    public boolean toggleExplosive(UUID uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM `explosion_prefs` WHERE `uuid` = ?");
            ps.setString(1,uuid.toString());
            rs = ps.executeQuery();
            boolean next = rs.next();
            boolean curr = !next || Boolean.parseBoolean(rs.getString("explosive"));
            // fix resource leak
            pool.close(conn, ps, rs);
            conn = pool.getConnection();
            ps = conn.prepareStatement(next?"UPDATE explosion_prefs SET explosive = ? WHERE uuid = ?":
            "INSERT INTO explosion_prefs (explosive, uuid) VALUES (?, ?)");
            ps.setString(1, String.valueOf(curr =! curr));
            ps.setString(2,uuid.toString());
            ps.executeUpdate();
            pool.close(conn, ps, rs);
            return curr;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void createExplosionTable() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS explosion_prefs (" +
            " `uuid` CHAR(36) NOT NULL," +
            " `explosive` VARCHAR(5) NOT NULL," +
            " PRIMARY KEY(`uuid`))");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
        }
    }

    public Boolean onJoin(String playername, UUID uuid) {
        boolean firstjoin = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM plasmaprison_users WHERE uuid = ?");
            ps.setString(1,uuid.toString());
            rs = ps.executeQuery();
            boolean b = rs.next();

            if (!b) {
                firstjoin = true;
            }
            // fix resource leak
            pool.close(conn, ps, rs);
            conn = pool.getConnection();
            ps = conn.prepareStatement(b ? "UPDATE plasmaprison_users SET name = ? WHERE uuid = ?" : "INSERT INTO plasmaprison_users (name, uuid) VALUES (?, ?)");
            ps.setString(1,playername);
            ps.setString(2,uuid.toString());
            ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
        }

        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM `plasmaprison_tokens` WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            rs = ps.executeQuery();
            boolean b = rs.next();
            if (!b) {
                Bukkit.getLogger().info("User "+playername+" does not exist in database, creating new entry...");
                pool.close(conn, ps, rs);
                conn = pool.getConnection();
                ps = conn.prepareStatement("INSERT INTO `plasmaprison_tokens` (uuid, tokens) VALUES (?,?)");
                ps.setString(1, uuid.toString());
                ps.setString(2, "0");
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
        }

        return firstjoin;
    }

    public void onLeave(UUID uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("INSERT INTO plasmaprison_tokens (uuid, tokens) VALUES (?, ?) ON DUPLICATE KEY UPDATE tokens = ?");
            ps.setString(1, uuid.toString());
            ps.setString(2, String.valueOf(this.plugin.tokens.get(uuid)));
            ps.setString(3, String.valueOf(this.plugin.tokens.get(uuid)));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
        }
    }

    public void saveTokens(UUID uuid, long amount) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM plasmaprison_tokens WHERE uuid = ?");
            ps.setString(1,uuid.toString());
            rs = ps.executeQuery();
            boolean b = rs.next();
            pool.close(conn, ps, rs);
            conn = pool.getConnection();
            ps = conn.prepareStatement(b ? "UPDATE `plasmaprison_tokens` SET `tokens` = ? WHERE `uuid` = ?" : "INSERT INTO `plasmaprison_tokens` (`tokens`,`uuid`) VALUES (?,?)");
            if (amount < 0) amount = 0;
            ps.setString(1, b ? String.valueOf(amount) : "0");
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
        } 
    }

    public void removeTokens(UUID uuid, long i) {
        if (this.plugin.tokens.containsKey(uuid)) {
            long tokens = this.plugin.tokens.get(uuid);
            if (tokens - i < 0) {
                this.plugin.tokens.put(uuid, 0L);
                return;
            }
            this.plugin.tokens.put(uuid, tokens - i);
        } else {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = pool.getConnection();
                ps = conn.prepareStatement("SELECT * FROM `plasmaprison_tokens` WHERE `uuid` = ?");
                ps.setString(1,uuid.toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    this.plugin.tokens.put(uuid, rs.getLong("tokens") - i);
                } else {
                    this.plugin.tokens.put(uuid, 0L);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                pool.close(conn, ps, rs);
            }

        }
    }

    public void addTokens(UUID uuid, long i, long max) {
        if (this.plugin.tokens.containsKey(uuid)) {
            long tokens = this.plugin.tokens.get(uuid);
            if (tokens + i > max) {
                this.plugin.tokens.put(uuid, max);
            } else {
                this.plugin.tokens.put(uuid, tokens + i);
            }
        } else {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = pool.getConnection();
                ps = conn.prepareStatement("SELECT * FROM `plasmaprison_tokens` WHERE `uuid` = ?");
                ps.setString(1,uuid.toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    if (rs.getLong("tokens") + i < 0 || rs.getLong("tokens") + i > max) {
                        this.plugin.tokens.put(uuid, 0L);
                    } else {
                        this.plugin.tokens.put(uuid, rs.getLong("tokens") + i);
                    }
                } else {
                    this.plugin.tokens.put(uuid, 0L);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                pool.close(conn, ps, rs);
            }
            
        }
    }

    public HashMap<UUID, HashMap<String, Integer>> getCellValues() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        HashMap<UUID, HashMap<String, Integer>> map = new HashMap<>();
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM virtualcellvalue");
            rs = ps.executeQuery();
            // convert uuid string to uuid object
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("owner"));
                String item_counts = rs.getString("item_counts");
                // convert json string to hashmap
                HashMap<String, Integer> item_counts_map = new Gson().fromJson(item_counts, new TypeToken<HashMap<String, Integer>>(){}.getType());
                map.put(uuid, item_counts_map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
            return map;
        }
    }

    public void removeCellValue(UUID uuid, String type, int amount) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        HashMap<UUID, HashMap<String, Integer>> map = new HashMap<>();
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM `virtualcellvalue` WHERE `owner` = ?");
            ps.setString(1, uuid.toString());
            rs = ps.executeQuery();
            // convert uuid string to uuid object
            if (rs.next()) {
                String item_counts = rs.getString("item_counts");
                // convert json string to hashmap
                HashMap<String, Integer> item_counts_map = new Gson().fromJson(item_counts, new TypeToken<HashMap<String, Integer>>(){}.getType());
                if (item_counts_map.containsKey(type)) {
                    int current = item_counts_map.get(type);
                    if (current - amount < 0) {
                        item_counts_map.put(type, 0);
                    } else {
                        item_counts_map.put(type, current - amount);
                    }
                } else {
                    item_counts_map.put(type, 0);
                }
                item_counts = new Gson().toJson(item_counts_map);

                pool.close(conn, ps, rs);
                conn = pool.getConnection();
                ps = conn.prepareStatement("UPDATE virtualcellvalue SET item_counts = ? WHERE owner = ?");
                ps.setString(1, item_counts);
                ps.setString(2, uuid.toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
        }
    }

    public long getTokens(UUID uuid, boolean fromDatabase) {
        if (!fromDatabase) {
            // get player by uuid
//             Bukkit.getLogger().info("§a§lDEBUG: §f§lUsing cached tokens");
            if (this.plugin.tokens.containsKey(uuid)) {
//                Bukkit.getLogger().info("Loaded tokens for " + uuid.toString() + ": " + this.plugin.tokens.get(uuid));
                return this.plugin.tokens.get(uuid);
            } else {
//                Bukkit.getLogger().info("Fetching from db...");
                fromDatabase = true; // fetch from db again
            }
        }

        if (fromDatabase) {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            long tokens = 0;
            try {
                conn = pool.getConnection();
                ps = conn.prepareStatement("SELECT * FROM plasmaprison_tokens WHERE uuid = ?");
                ps.setString(1, uuid.toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    tokens = rs.getLong("tokens");
                } else {
                    pool.close(conn, ps, rs);
                    conn = pool.getConnection();
                    ps = conn.prepareStatement("INSERT INTO `plasmaprison_tokens` (`tokens`,`uuid`) VALUES (?,?)");
                    ps.setString(1, "0");
                    ps.setString(2, uuid.toString());
                    ps.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                pool.close(conn, ps, rs);
//                Bukkit.getLogger().info("Loaded tokens for " + uuid.toString() + ": " + tokens);
                return tokens;
            }
        }

        Bukkit.getLogger().severe("§c§lERROR: §f§lPlayer not found in cache or database! TELL MyNqme IMMEDIATELY (Extra info: " + uuid.toString() + ")");
        return 0;
    }

    public void setTokens(UUID uuid, long i) {
        Bukkit.getLogger().info("updating tokens to " + i + " for " + uuid.toString() + "...");
        if (this.plugin.tokens.containsKey(uuid)) {
            this.plugin.tokens.put(uuid, i);
        } else {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = pool.getConnection();
                ps = conn.prepareStatement("UPDATE `plasmaprison_tokens` SET `tokens` = ? WHERE `uuid` = ?");
                ps.setString(1, String.valueOf(i));
                ps.setString(1, uuid.toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    this.plugin.tokens.put(uuid, i);
                } else {
                    this.plugin.tokens.put(uuid, i);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                pool.close(conn, ps, rs);
                Bukkit.getLogger().info("Updated to: " + this.plugin.tokens.get(uuid));
            }
        }


    }

    public void createTokensTable() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `plasmaprison_tokens` (`uuid` VARCHAR(36) NOT NULL, `tokens` TEXT NOT NULL, PRIMARY KEY (`uuid`));");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public void createUsersTable() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `plasmaprison_users` (`uuid` VARCHAR(36) NOT NULL, `name` TEXT NOT NULL, PRIMARY KEY (`uuid`));");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public void closeConnections() {
        pool.closePool();
    }

    public static Database inst() {
        return inst;
    }
}
