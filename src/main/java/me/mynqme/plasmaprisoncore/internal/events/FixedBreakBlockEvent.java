package me.mynqme.plasmaprisoncore.internal.events;

import me.mynqme.plasmaprisoncore.enchant.BreakResult;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class FixedBreakBlockEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final Player player;
    private final List<BreakResult> results;
    public FixedBreakBlockEvent(Player player, List<BreakResult> results) {
        this.player = player;
        this.results = results;
    }

    public Player getPlayer() {
        return player;
    }

    public List<BreakResult> getResults() {
        return results;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
