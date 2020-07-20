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
    public abstract void load();

    public void unload(String addon) {
        instance.getLogger().info("(!) Unloading " + addon + " addon.");
        for (Listener listener : listeners) HandlerList.unregisterAll(listener);
    }

    public void registerListeners(Listener... listeners) {
        this.listeners = listeners;
        for (Listener listener : listeners) Bukkit.getPluginManager().registerEvents(listener,instance);
    }
}
