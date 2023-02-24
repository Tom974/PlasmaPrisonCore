package me.mynqme.plasmaprisoncore.util;

import me.mynqme.plasmaprisoncore.PlasmaPrisonCore;
import org.bukkit.Bukkit;

public class AsyncTask {
    public AsyncTask(Runnable runnable){
        PlasmaPrisonCore instance = PlasmaPrisonCore.getInstance();
        Bukkit.getScheduler().runTaskAsynchronously(instance, runnable);
    }
}
