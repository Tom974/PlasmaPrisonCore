package me.fede1132.plasmaprisoncore.internal.util;

import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private final PlasmaPrisonCore instance;
    public Database(PlasmaPrisonCore instance) {
        this.instance = instance;
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
        File db = new File(instance.getDataFolder(),"database.db");
        try {
            if (!db.exists()) db.createNewFile();
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:" + db);
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
