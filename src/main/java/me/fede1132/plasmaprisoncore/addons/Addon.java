package me.fede1132.plasmaprisoncore.addons;

import de.leonhard.storage.Yaml;
import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import me.fede1132.plasmaprisoncore.addons.cmds.XCommand;
import me.fede1132.plasmaprisoncore.addons.enchant.Enchant;
import me.fede1132.plasmaprisoncore.internal.util.SimpleEntry;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class Addon {
    private static Addon instance;
    public PlasmaPrisonCore plugin = PlasmaPrisonCore.getInstance();
    private String addon;
    public boolean isEnabled = true;
    private Listener[] listeners;
    private String[] enchants;
    private List<String> dependencies;

    public void init(String addon) {
        instance = this;
        this.addon = addon;
        load();
    }

    public abstract void load();

    public void unload() {
        plugin.getLogger().info("(!) Unloading " + addon + " addon.");
        if(listeners!=null) for (Listener listener : listeners) HandlerList.unregisterAll(listener);
        if(enchants!=null) for (String enchant : enchants) plugin.enchantManager.registeredEnchants.remove(enchant);
    }

    public void registerListeners(Listener... listeners) {
        this.listeners = listeners;
        for (Listener listener : listeners) Bukkit.getPluginManager().registerEvents(listener,plugin);
    }

    public void registerCommands(XCommand... commands) {
        // TODO
    }

    public void addDependencies(String... depend) {
        this.dependencies = Arrays.asList(depend);
    }

    public void registerEnchants(Enchant... enchants) {
        this.enchants = new String[enchants.length];
        Yaml yaml = setupPersonalConfig("enchants", null);
        HashMap<String, Enchant> map = new HashMap<>();
        for (int i=0;i<enchants.length;i++) {
            Enchant enchant = enchants[i];
            this.enchants[i] = enchant.getId();
            String path = "enchants." + enchant.getId() + ".";

            if (!yaml.contains(path+"lore-color")) yaml.setDefault(path+"lore-color", enchant.loreColor);
            else enchant.loreColor = yaml.getString(path+"lore-color");

            if (!yaml.contains(path+"max-level")) yaml.setDefault(path+"max-level", enchant.max);
            else enchant.max = yaml.getInt(path+"max-level");

            if (!yaml.contains(path+"cost")) yaml.setDefault(path+"cost", enchant.cost);
            else enchant.cost = yaml.getInt(path+"cost");

            if (enchant.options!=null) for (int j=0;j<enchant.options.length;j++) {
                SimpleEntry<String,Object> option = enchant.options[j];
                if (!yaml.contains(path+option.getKey())) yaml.setDefault(path+option.getKey(),option.getValue());
                else option.setValue(yaml.get(path+option.getKey()));
                enchant.options[j] = option;
            }

            map.put(enchant.getId(),enchant);
        }
        plugin.enchantManager.registeredEnchants.putAll(map);
    }

    public Yaml setupPersonalConfig(String name, HashMap<String, Object> def) {
        String separator = File.separator;
        Yaml yaml = new Yaml(new File(plugin.getDataFolder()+separator+"configs"+separator+addon, name));
        if (def!=null&&def.size()>0) def.forEach(yaml::setDefault);
        return yaml;
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

    public List<String> getDependencies() {
        return dependencies;
    }

    public static Addon getInstance() {
        return instance;
    }
}
