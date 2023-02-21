package me.fede1132.plasmaprisoncore.internal.util.sql;

import java.sql.SQLException;

import java.sql.Connection;
import java.sql.ResultSet;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;

public class ConnectionPoolManager {
 
    private final PlasmaPrisonCore plugin;
    private HikariDataSource dataSource;
 
    public ConnectionPoolManager(PlasmaPrisonCore plugin) {
        this.plugin = plugin;
        setupPool();
    }

    private void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + "172.18.0.1" + ":" + "3306" + "/" + "prison");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername("plugins");
        config.setPassword("@%!agea3aUNG!3");
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