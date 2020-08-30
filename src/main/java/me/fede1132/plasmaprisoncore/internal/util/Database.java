package me.fede1132.plasmaprisoncore.internal.util;

import com.zaxxer.hikari.HikariDataSource;
import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class Database {
    private static Database inst;
    private final HikariDataSource ds = new HikariDataSource();
    public Database(PlasmaPrisonCore instance) {
        inst = this;
        File db = new File(instance.getDataFolder(),"database.db");
        if (!db.exists()) {
            instance.getDataFolder().mkdirs();
            try {
                db.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ds.setJdbcUrl("jdbc:sqlite:"+db);
        ds.setMaximumPoolSize(25);

        try (Connection connection = getConnection()) {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS users (\n" +
                    "  `uuid` CHAR(36) NOT NULL,\n" +
                    "  `name` VARCHAR(16) NOT NULL,\n" +
                    "  PRIMARY KEY (`uuid`));\n").executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Database inst() {
        return inst;
    }
}
