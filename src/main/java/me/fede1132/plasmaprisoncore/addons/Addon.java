package me.fede1132.plasmaprisoncore.addons;

import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.Yaml;
import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import me.fede1132.plasmaprisoncore.addons.enchant.Enchant;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.HashMap;

public abstract class Addon {
    public PlasmaPrisonCore instance = PlasmaPrisonCore.getInstance();
    private String addon;
    public boolean isEnabled = true;
    public Yaml config;
    private Listener[] listeners;
    private String[] enchants;

    public void init(String addon) {
        this.addon = addon;
    }

    public abstract void load();

    public void unload() {
        instance.getLogger().info("(!) Unloading " + addon + " addon.");
        for (Listener listener : listeners) HandlerList.unregisterAll(listener);
        for (String enchant : enchants) instance.enchantManager.registeredEnchants.remove(enchant);
    }

    public void registerListeners(Listener... listeners) {
        this.listeners = listeners;
        for (Listener listener : listeners) Bukkit.getPluginManager().registerEvents(listener,instance);
    }

    public void registerEnchants(Enchant... enchants) {
        this.enchants = new String[enchants.length];
        boolean n = setupPersonalConfig(null);
        HashMap<String, Enchant> map = new HashMap<>();
        for (int i=0;i<enchants.length;i++) {
            Enchant enchant = enchants[i];
            this.enchants[i] = enchant.getId();
            String path = "enchants." + enchant.getId() + ".";
            config.setDefault(path+"lore-color", enchant.loreColor);
            config.setDefault(path+"max-level", enchant.max);
            config.setDefault(path+"cost", enchant.cost);
            if (!n) {
                enchant.loreColor = config.getString(path+"lore-color");
                enchant.max = config.getInt(path+"max-level");
                enchant.cost = config.getInt(path+"cost");
            }
            map.put(enchant.getId(),enchant);
        }
        instance.enchantManager.registeredEnchants.putAll(map);
    }

    public boolean setupPersonalConfig(HashMap<String, Object> def) {
        File file = new File(instance.getDataFolder()
                + System.getProperty("path.separator") + "configs", addon);
        boolean n = file.exists();
        if (!n) {
            config = LightningBuilder.fromFile(file).createYaml();
            if (def!=null&&def.size()>0) def.forEach(config::set);
        }
        return n;
    }

    public void enable() {
        if (!isEnabled) {
            load();
            isEnabled=true;
        }
    }

    public void disable() {
        if (isEnabled) {
            unload();
            isEnabled=false;
        }
    }
}
