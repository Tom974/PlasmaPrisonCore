package me.fede1132.testaddon;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class TestListener implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        event.getPlayer().sendMessage("I'm working b*tches!");
    }
}
