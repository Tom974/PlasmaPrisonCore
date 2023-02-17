package me.fede1132.plasmaprisoncore;

import de.leonhard.storage.SimplixBuilder;
import de.leonhard.storage.Yaml;
import de.leonhard.storage.internal.settings.ConfigSettings;
import me.fede1132.plasmaprisoncore.addons.AddonManager;
import me.fede1132.plasmaprisoncore.addons.basics.AddonBasics;
import me.fede1132.plasmaprisoncore.enchant.EnchantManager;
import me.fede1132.plasmaprisoncore.events.*;
import me.fede1132.plasmaprisoncore.internal.hooks.HookPapi;
import me.fede1132.plasmaprisoncore.internal.util.sql.Database;
import me.fede1132.plasmaprisoncore.util.Tasks;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.UUID;

public final class PlasmaPrisonCore extends JavaPlugin {
    private static PlasmaPrisonCore instance;
    public Database database;
    public AddonManager addonManager;
    public EnchantManager enchantManager;
    public Yaml config;
    public Yaml chat;
    public Yaml messages;
    public Economy econ;
    public HashMap<UUID, Long> tokens = new HashMap<>();

    public static PlasmaPrisonCore getCore() {
        return instance;
    }

    @Override
    public void onEnable() {
        log("Plasma Prison Core v" + getDescription().getVersion());
        log("Loading files..");
        database = new Database(this);
        config = SimplixBuilder.fromFile(new File(getDataFolder(),"config")).addInputStream(getResource("config.yml")).setConfigSettings(ConfigSettings.PRESERVE_COMMENTS).createYaml();
        chat = SimplixBuilder.fromFile(new File(getDataFolder(),"chat")).addInputStream(getResource("chat.yml")).setConfigSettings(ConfigSettings.PRESERVE_COMMENTS).createYaml();
        messages = SimplixBuilder.fromFile(new File(getDataFolder(), "messages")).addInputStream(getResource("messages.yml")).setConfigSettings(ConfigSettings.PRESERVE_COMMENTS).createYaml();
        log("Loading command and hooks..");
        instance = this;
        log("Loading tokens from players..");
        for (Player p : Bukkit.getOnlinePlayers()) {
            this.tokens.put(p.getUniqueId(), this.database.getTokens(p.getUniqueId(), true));
        }
        log("Loading events..");
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerJoin(), this);
        pm.registerEvents(new PlayerLeave(), this);
        pm.registerEvents(new RightClick(), this);
        pm.registerEvents(new BlockBreak(), this);
        pm.registerEvents(new CommandPreProcess(), this);
        log("Loading vault lib");
        setupVault();
        log("Starting tasks...");
        new Tasks(this);
        log("Loading managers..");
        enchantManager = new EnchantManager();
        addonManager = new AddonManager();
        Bukkit.getScheduler().runTaskAsynchronously(this, ()->{
            new AddonBasics().init("Basics");
            instance.getLogger().info("(!) Successfully loaded addon Basics");
            addonManager.reloadAddons();
        });
        new HookPapi().register();
    }

    @Override
    public void onDisable() {
        log("Disabling plugin..");
        log("Saving tokens to database..");
        for (UUID uuid : this.tokens.keySet()) {
            this.database.saveTokens(uuid, this.tokens.get(uuid));
        }
        database.closeConnections();
    }

    public void setupVault() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp==null||rsp.getProvider()==null) {
            getLogger().severe("AN ERROR OCCURRED WHILE ENABLING VAULT, SHUTTING DOWN...");
            Bukkit.getServer().shutdown();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        econ = rsp.getProvider();
    }

    private void log(String msg) {
        getLogger().info("(!) "+msg);
    }

    public static PlasmaPrisonCore getInstance() {
        return instance;
    }

    public void addClassPath(final URL url) throws IOException {
        final URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        final Class<URLClassLoader> sysclass = URLClassLoader.class;
        try {
            final Method method = sysclass.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(sysloader, url);
        } catch (final Throwable t) {
            t.printStackTrace();
            throw new IOException("Error adding " + url + " to system classloader");
        }
    }
}
