package me.fede1132.plasmaprisoncore.enchant;

import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

public class AddonManager {
    private final PlasmaPrisonCore instance = PlasmaPrisonCore.getInstance();
    public HashMap<String, Object> addons = new HashMap<>();
    public void reloadAddons() {
        try {
            if (!addons.isEmpty()) {
                addons.forEach((k, v) -> {
                    ((Addon) v).unload(k);
                    addons.remove(k);
                });
            }
            File folder = new File(instance.getDataFolder(), "addons/");
            if (!folder.exists()) {
                folder.mkdir();
                return;
            }
            File[] addons = folder.listFiles();
            for (int i = 0; i < addons.length; i++) {
                File addon = addons[i];
                URLClassLoader uc = new URLClassLoader(new URL[]{addon.toURI().toURL()}, instance.getClass().getClassLoader());
                URL yaml = uc.getResource("addon.yml");
                if (yaml == null) {
                    warnlog(addon, "Cannot find addon.yml");
                    return;
                }
                InputStreamReader stream = new InputStreamReader(yaml.openStream());
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(stream);
                if (!cfg.contains("name") || !cfg.contains("main")) {
                    warnlog(addon, "The addon.yml does not contains name or main strings");
                    return;
                }
                String clazz = cfg.getString("main");
                System.out.println("Clazz: " + clazz);
                Class.forName(Addon.class.getName());
                Class<?> wrap = Class.forName(clazz, true, uc);
                if (!Addon.class.isAssignableFrom(wrap)) {
                    warnlog(addon, "Could not find a valid Addon class at path " + clazz);
                    return;
                }
                Addon instance = (Addon) wrap.newInstance();
                instance.load();
                this.addons.put(cfg.getString("name"), instance);
                this.instance.getLogger().info("(!) Successfully loaded addon " + addon.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void warnlog(File file, String msg) {
        instance.getLogger().warning("(!) Could not load enchant addon " + file.getName());
        instance.getLogger().warning("(!) Cause: "+msg);
    }

    public static URL getJarUrl(final File file) throws IOException {
        return new URL("jar:" + file.toURI().toURL().toExternalForm() + "!/");
    }
}
