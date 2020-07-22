package me.fede1132.plasmaprisoncore.addons;

import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import me.fede1132.plasmaprisoncore.internal.util.SimpleEntry;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class AddonManager {
    public static class CachedAddon {
        private final URLClassLoader loader;
        private final Addon main;
        public CachedAddon(URLClassLoader loader, Addon main) {
            this.loader = loader;
            this.main = main;
        }

        public void closeLoader() {
            try {
                loader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Addon getMain() {
            return main;
        }
    }
    private final PlasmaPrisonCore instance = PlasmaPrisonCore.getInstance();
    public HashMap<String, CachedAddon> addons = new HashMap<>();
    public void reloadAddons() {
        try {
            if (!addons.isEmpty()) {
                Iterator<Map.Entry<String,CachedAddon>> iterator = addons.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String,CachedAddon> entry = iterator.next();
                    entry.getValue().getMain().unload();
                    entry.getValue().closeLoader();
                    iterator.remove();
                }
            }
            File folder = new File(instance.getDataFolder(), "addons/");
            if (!folder.exists()) {
                folder.mkdir();
                return;
            }
            File[] addons = folder.listFiles();
            List<SimpleEntry<URLClassLoader, YamlConfiguration>> list = new ArrayList<>();
            for (int i = 0; i < addons.length; i++) {
                File addon = addons[i];
                if (!addon.getName().endsWith(".jar")) continue;
                URLClassLoader uc = new URLClassLoader(new URL[]{addon.toURI().toURL()}, instance.getClass().getClassLoader());
                InputStream yaml = uc.getResourceAsStream("addon.yml");
                if (yaml==null) {
                    warnLog(addon, "Cannot find addon.yml");
                    return;
                }
                InputStreamReader stream = new InputStreamReader(yaml);
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(stream);
                if (!cfg.contains("name") || !cfg.contains("main")) {
                    warnLog(addon, "The addon.yml does not contains name or main strings");
                    stream.close();
                    return;
                }
                stream.close();
                yaml.close();
                if (cfg.contains("priority")) list.add(cfg.getInt("priority"), new SimpleEntry<>(uc,cfg));
                else list.add(new SimpleEntry<>(uc,cfg));
            }
            list.forEach(entry->{
                try {
                    String name = entry.getValue().getString("name");
                    String clazz = entry.getValue().getString("main");
                    Class<?> wrap = Class.forName(clazz, true, entry.getKey());
                    if (!Addon.class.isAssignableFrom(wrap)) {
                        warnLog(name, "Could not find a valid Addon class at path " + clazz);
                        return;
                    }
                    Addon instance = (Addon) wrap.newInstance();
                    instance.init(name);
                    this.addons.put(name, new CachedAddon(entry.getKey(), instance));
                    this.instance.getLogger().info("(!) Successfully loaded addon " + name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unload(String addon) {
        CachedAddon cache = addons.get(addon);
        addons.values().stream().filter(filter->filter.getMain().getDependencies()!=null&&filter.getMain().getDependencies().contains(addon)).forEach(action->action.getMain().disable());
        cache.getMain().disable();
    }

    public boolean isAddonEnabled(String addon) {
        return addons.containsKey(addon)&&addons.get(addon).getMain().isEnabled;
    }

    private void warnLog(Object file, String msg) {
        if (file instanceof File) file = ((File) file).getName();
        instance.getLogger().warning("(!) Could not load enchant addon " + file);
        instance.getLogger().warning("(!) Cause: "+msg);
    }
}
