package me.fede1132.plasmaprisoncore.enchant;

import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class Addon {
    public PlasmaPrisonCore instance = PlasmaPrisonCore.getInstance();
    private Listener[] listeners;
    private List<HandlerList> handlers = new ArrayList<>();
    public abstract void load();

    public void unload(String addon) {
        instance.getLogger().info("(!) Unloading " + addon + " addon.");
        handlers.forEach(handlerList -> {
            for (Listener listener : listeners) handlerList.unregister(listener);
        });
    }

    public void registerListeners(Listener... listeners) {
        this.listeners = listeners;
        for (Listener listener : listeners) {
            for (Method method : listener.getClass().getMethods()) {
                if (method.isAnnotationPresent(EventHandler.class)) {
                    for (Class<?> clazz : method.getParameterTypes()) {
                        if (clazz.isAssignableFrom(Event.class)) {
                            handlers.add(Event.class.cast(clazz).getHandlers());
                        }
                    }
                }
            }
            Bukkit.getPluginManager().registerEvents(listener,instance);
        }
    }
}
