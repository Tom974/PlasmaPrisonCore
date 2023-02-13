package me.fede1132.plasmaprisoncore.util;

import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import org.bukkit.Bukkit;

public class AsyncTask {
    public AsyncTask(Runnable runnable){
        PlasmaPrisonCore instance = PlasmaPrisonCore.getInstance();
        Bukkit.getScheduler().runTaskAsynchronously(instance, runnable);
    }
}
