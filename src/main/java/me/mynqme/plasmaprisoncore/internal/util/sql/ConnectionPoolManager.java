package me.mynqme.plasmaprisoncore.internal.util.sql;

import java.sql.SQLException;

import java.sql.Connection;
import java.sql.ResultSet;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import me.mynqme.plasmaprisoncore.PlasmaPrisonCore;

public class ConnectionPoolManager {
 
    private final PlasmaPrisonCore plugin;
    private HikariDataSource dataSource;
 
    public ConnectionPoolManager(PlasmaPrisonCore plugin) {
        this.plugin = plugin;
        setupPool();
    }

    private void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + this.plugin.config.getString("database.host") + ":" + this.plugin.config.getString("database.port") + "/" + this.plugin.config.getString("database.database"));
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(this.plugin.config.getString("database.username"));
        config.setPassword(this.plugin.config.getString("database.password"));
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(100);
        config.setConnectionTimeout(300);
        dataSource = new HikariDataSource(config);
    }
 
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
 
    public void close(Connection conn, java.sql.PreparedStatement ps, ResultSet res) {
        if (conn != null) try { conn.close(); } catch (SQLException ignored) {}
        if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        if (res != null) try { res.close(); } catch (SQLException ignored) {}
    }
 
    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}