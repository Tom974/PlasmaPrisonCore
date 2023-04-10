package me.mynqme.plasmaprisoncore.addons;

import de.leonhard.storage.Yaml;
import me.mynqme.plasmaprisoncore.PlasmaPrisonCore;
import me.mynqme.plasmaprisoncore.addons.cmds.XCommand;
import me.mynqme.plasmaprisoncore.enchant.Enchant;
import me.mynqme.plasmaprisoncore.enchant.EnchantManager;
// import me.mynqme.plasmaprisoncore.internal.hooks.PapiPlaceholder;
import me.mynqme.plasmaprisoncore.internal.util.SimpleEntry;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
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
        Bukkit.getScheduler().runTaskAsynchronously(plugin, ()->{
            try {
                final Field commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                commandMap.setAccessible(true);
                CommandMap map = (CommandMap) commandMap.get(Bukkit.getServer());
                Arrays.stream(commands).forEach(cmd->{
                    map.register(cmd.getName(), cmd);
                    plugin.getLogger().info("[PlasmaPrison - " + addon + "] Registered command /" + cmd.getName());
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // public void registerPapiPlaceholder(PapiPlaceholder... placeholders) {
    //     HookPapi.inst().placeholders.addAll(Arrays.asList(placeholders));
    // }

    public void registerEnchants(Enchant... enchants) {
        this.enchants = new String[enchants.length];
        Yaml yaml = setupPersonalConfig("enchants", null);
        List<Enchant> list = new ArrayList<>();
        for (int i=0;i<enchants.length;i++) {
            Enchant enchant = enchants[i];
            this.enchants[i] = enchant.getId();
            String path = "enchants." + enchant.getId() + ".";
            enchant.displayName = yaml.getOrSetDefault(path+"displayname", enchant.displayName);
            enchant.loreColor = yaml.getOrSetDefault(path+"lore-color", enchant.loreColor);
            enchant.max = yaml.getOrSetDefault(path+"max", enchant.max);
            enchant.cost = yaml.getOrSetDefault(path+"cost", enchant.cost);
            enchant.maxChance = yaml.getOrSetDefault(path+"max-chance", enchant.maxChance);
            enchant.refundPercent = yaml.getOrSetDefault(path+"refund-percent", enchant.refundPercent);
            if (enchant.options != null) for (int j = 0; j < enchant.options.length; j++) {
                SimpleEntry<String,Object> option = enchant.options[j];
                option.setValue(yaml.getOrSetDefault(path+option.getKey(), option.getValue()));
                enchant.options[j] = option;
            }
            list.add(enchant);
        }
        EnchantManager manager = plugin.enchantManager;
        list.forEach(manager::register);
    }

    public String[] getEnchants() {
        return enchants;
    }

    public Yaml setupPersonalConfig(String name, SimpleEntry<String, Object>... def) {
        String separator = File.separator;
        Yaml yaml = new Yaml(new File(plugin.getDataFolder()+separator+"configs"+separator+addon, name));
        if (def!=null&&def.length>0) Arrays.stream(def).forEach(value->yaml.setDefault(value.getKey(), value.getValue()));
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
