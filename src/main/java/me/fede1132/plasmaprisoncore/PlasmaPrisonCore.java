package me.fede1132.plasmaprisoncore;

import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.Yaml;
import de.leonhard.storage.internal.settings.ConfigSettings;
import me.fede1132.plasmaprisoncore.events.BlockBreak;
import me.fede1132.plasmaprisoncore.events.EconomyChange;
import me.fede1132.plasmaprisoncore.util.Tasks;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class PlasmaPrisonCore extends JavaPlugin {
    private static PlasmaPrisonCore instance;
    // Files
    public Yaml config;
    public Yaml chat;
    public Yaml messages;
    // Vault
    public Economy econ;
    @Override
    public void onEnable() {
        log("Plasma Prison Core v" + getDescription().getVersion());
        log("Loading files..");
        config = LightningBuilder.fromFile(new File(getDataFolder(),"config")).addInputStream(getResource("config.yml")).setConfigSettings(ConfigSettings.PRESERVE_COMMENTS).createYaml();
        chat = LightningBuilder.fromFile(new File(getDataFolder(),"chat")).addInputStream(getResource("chat.yml")).setConfigSettings(ConfigSettings.PRESERVE_COMMENTS).createYaml();;
        messages = LightningBuilder.fromFile(new File(getDataFolder(), "messages")).addInputStream(getResource("messages.yml")).setConfigSettings(ConfigSettings.PRESERVE_COMMENTS).createYaml();
        log("Loading command and hooks..");
        instance = this;
        log("Loading events..");
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new BlockBreak(), this);
        pm.registerEvents(new EconomyChange(), this);
        log("Loading vault lib");
        setupVault();
        log("Starting tasks...");
        new Tasks(this);
    }

    @Override
    public void onDisable() {
        log("Disabling plugin..");
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
}
