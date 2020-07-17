package me.fede1132.plasmaprisoncore.enchant;

import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import me.fede1132.plasmaprisoncore.internal.util.ResourceList;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Pattern;

public class AddonManager {
    private final PlasmaPrisonCore instance = PlasmaPrisonCore.getInstance();
    private HashMap<String, Object> addons = new HashMap<>();
    public void reloadAddons() throws Exception {
        if (!addons.isEmpty()) {
            addons.forEach((k,v)->{
                ((Addon) v).unload(k);
            });
        }
        File folder = new File(instance.getDataFolder(),"enchants/");
        if (!folder.exists()) {
            folder.mkdir();
            return;
        }
        File[] addons = folder.listFiles();
        for (int i=0;i<addons.length;i++) {
            File addon = addons[i];
            URLClassLoader uc = createURLClassLoader(addon);
            InputStream yaml = uc.getResourceAsStream("addon.yml");
            if (yaml==null) {
                warnlog(addon,"Cannot find addon.yml");
                return;
            }
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(new InputStreamReader(yaml));
            if (!cfg.contains("name")||!cfg.contains("main")) {
                warnlog(addon,"The addon.yml does not contains name or main strings");
                return;
            }
            String clazz = cfg.getString("name");
            Class<?> main = Class.forName(clazz, true, uc);
            if (!main.isAssignableFrom(Addon.class)) {
                warnlog(addon,"Could not find a valid Addon class at path " + clazz);
                return;
            }
            Object instance = main.newInstance();
            Method load = main.getMethod("load");
            load.invoke(instance);
            this.instance.getLogger().info("(!) Successfully loaded addon " + addon.getName());
        }
    }


    private URLClassLoader createURLClassLoader(File addon) {
        Collection<String> resources = ResourceList.getResources(Pattern.compile(".*\\.jar"));
        Collection<URL> urls = new ArrayList<URL>();
        for (String resource : resources) {
            File file = new File(resource);
            // Ensure that the JAR exists
            // and is in the addons directory
            if (file.isFile() && "addons".equals(file.getParentFile().getName())) {
                try {
                    urls.add(file.toURI().toURL());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        return new URLClassLoader(urls.toArray(new URL[urls.size()]));
    }

    private void warnlog(File file, String msg) {
        instance.getLogger().warning("(!) Could not load enchant addon " + file.getName());
        instance.getLogger().warning("(!) Cause: "+msg);
    }
}
