package me.fede1132.plasmaprisoncore.internal.util.sql;

import com.zaxxer.hikari.HikariDataSource;
import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Database {
    private static Database inst;
    private final ConnectionPoolManager pool;
    private final PlasmaPrisonCore plugin;
    public Database(PlasmaPrisonCore instance) {
        this.plugin = instance;
        inst = this;
        pool = new ConnectionPoolManager(plugin);
    }

    public void onJoin(String playername, UUID uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM plasmaprison_users WHERE uuid = ?");
            ps.setString(1,uuid.toString());
            rs = ps.executeQuery();
            boolean b = rs.next();
            if (b) {
                String dbname = rs.getString("name");
                if (dbname.equalsIgnoreCase(playername)) {
                    pool.close(conn, ps, rs);
                    return;
                }
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
    }

    public void removeTokens(UUID uuid, long i) {
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
            i = rs.getLong("tokens") - i;
            if (i < 0) i = 0;
            ps.setString(1, b ? String.valueOf(i) : "0");
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
        }
    }

    public void addTokens(UUID uuid, long i, long max) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM plasmaprison_tokens WHERE uuid = ?");
            ps.setString(1,uuid.toString());
            rs = ps.executeQuery();
            boolean b = rs.next();
            i = rs.getLong("tokens") + i;
            pool.close(conn, ps, rs);
            conn = pool.getConnection();
            ps = conn.prepareStatement(b ? "UPDATE `plasmaprison_tokens` SET `tokens` = ? WHERE `uuid` = ?" : "INSERT INTO `plasmaprison_tokens` (`tokens`,`uuid`) VALUES (?,?)");
            ps.setString(1, String.valueOf(i));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
        }
    }

    public long getTokens(UUID uuid) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
        }

        return tokens;
    }

    public void setTokens(UUID uuid, long i) {
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
            ps.setString(1, String.valueOf(i));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
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
